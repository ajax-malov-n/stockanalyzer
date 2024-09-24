package systems.ajax.malov.stockanalyzer.repository.impl

import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Avg
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.ConvertOperators.ToDecimal.toDecimal
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.asType
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import java.math.BigDecimal
import java.util.Date
import org.springframework.data.mongodb.core.mapping.Document as SpringDocument

@Repository
class MongoStockRecordRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : StockRecordRepository {
    override fun insertAll(mongoStockRecords: List<MongoStockRecord>): Flux<MongoStockRecord> {
        return reactiveMongoTemplate.insertAll(mongoStockRecords)
    }

    override fun findAllStockSymbols(): Mono<List<String>> {
        return reactiveMongoTemplate.query(MongoStockRecord::class.java)
            .distinct(MongoStockRecord::symbol.name)
            .asType<String>()
            .all()
            .collectList()
    }

    override fun findTopNStockSymbolsWithStockRecords(
        n: Int,
        from: Date,
        to: Date,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        val maxChangeMono = getMaxBigDecimal(MongoStockRecord::change.name, from, to)
        val maxPercentChangeMono = getMaxBigDecimal(MongoStockRecord::percentChange.name, from, to)

        return Mono.zip(maxChangeMono, maxPercentChangeMono)
            .flatMap { maxValues ->
                val (maxChange, maxPercentChange) = maxValues
                fetchNBestStockSymbolsWithStockRecords(from, to, maxChange, maxPercentChange, n)
            }
            .map { results ->
                getOnlyMostRecentNDataRecords(
                    results.flatMap { it.records }
                        .groupBy { it.symbol ?: "Not provided" }
                )
            }
    }

    private fun fetchNBestStockSymbolsWithStockRecords(
        from: Date,
        to: Date,
        maxChange: BigDecimal,
        maxPercentChange: BigDecimal,
        n: Int,
    ): Mono<MutableList<ResultingClass>> {
        val matchOperation: MatchOperation =
            Aggregation.match(
                Criteria.where(MongoStockRecord::dateOfRetrieval.name)
                    .gte(from)
                    .lt(to)
            )
        val groupOperation: GroupOperation = Aggregation.group(MongoStockRecord::symbol.name)
            .push("$\$ROOT").`as`("records")
        val projectOperation = getProjectWithAvgMaxValues(maxChange, maxPercentChange)
        val weightedPipeline = getWeightedPipeLine()

        val aggregation = Aggregation.newAggregation(
            matchOperation,
            groupOperation,
            projectOperation,
            weightedPipeline,
            Aggregation.sort(Sort.by("weight").descending()),
            Aggregation.limit(n.toLong())
        )

        return reactiveMongoTemplate.aggregate(
            aggregation,
            MongoStockRecord.COLLECTION_NAME,
            ResultingClass::class.java
        ).collectList()
    }

    private fun getProjectWithAvgMaxValues(
        maxChange: BigDecimal,
        maxPercentChange: BigDecimal,
    ): ProjectionOperation {
        return Aggregation.project()
            .andInclude("records")
            .andExclude("_id")
            .and(getAvgOfRecordsArrayField(MongoStockRecord::change.name))
            .`as`("avgChange")
            .and(getAvgOfRecordsArrayField(MongoStockRecord::percentChange.name))
            .`as`("avgPercentChange")
            .and(toDecimal(maxChange))
            .`as`("maxChange")
            .and(toDecimal(maxPercentChange))
            .`as`("maxPercentChange")
            .and(toDecimal(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS.toBigDecimal()))
            .`as`("changeCoef")
            .and(toDecimal(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS.toBigDecimal()))
            .`as`("percentChangeCoef")
    }

    private fun getAvgOfRecordsArrayField(field: String): Avg {
        val mapRecordsPercentChange = AggregationExpression { _ ->
            Document(
                "\$map",
                Document("input", "\$records")
                    .append("as", "record")
                    .append("in", "$\$record.$field")
            )
        }
        return Avg.avgOf(mapRecordsPercentChange)
    }

    private fun getWeightedPipeLine(): ProjectionOperation {
        return Aggregation.project("records")
            .andExpression(
                "(avgChange / maxChange) * changeCoef + " +
                    "(avgPercentChange / maxPercentChange) * percentChangeCoef"
            )
            .`as`("weight")
    }

    private fun getMaxBigDecimal(
        field: String,
        from: Date,
        to: Date,
    ): Mono<BigDecimal> {
        val matchDateRange = Aggregation.match(
            Criteria.where(MongoStockRecord::dateOfRetrieval.name)
                .gte(from)
                .lt(to)
        )

        val matchNonNullField = Aggregation.match(
            Criteria.where(field).ne(null)
        )

        val project = Aggregation.project(field)
            .andExclude("_id")
            .and(field).`as`("max")

        val sort = Aggregation.sort(Sort.Direction.DESC, field)

        val limit = Aggregation.limit(1)

        val aggregation = Aggregation.newAggregation(matchDateRange, matchNonNullField, project, sort, limit)

        return reactiveMongoTemplate
            .aggregate(aggregation, MongoStockRecord.COLLECTION_NAME, AggregatedBigDecimalResult::class.java)
            .next()
            .map { it.max }
    }

    private fun getOnlyMostRecentNDataRecords(
        map: Map<String, List<MongoStockRecord>>,
    ): Map<String, List<MongoStockRecord>> {
        return map.mapValues {
            it.value.sortedByDescending { stockRecord -> stockRecord.dateOfRetrieval }
                .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL)
        }
    }

    internal data class AggregatedBigDecimalResult(
        val max: BigDecimal,
    )

    @SpringDocument
    internal data class ResultingClass(val records: List<MongoStockRecord>)

    companion object {
        private const val NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL = 5
        private const val WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS = 0.5
    }
}
