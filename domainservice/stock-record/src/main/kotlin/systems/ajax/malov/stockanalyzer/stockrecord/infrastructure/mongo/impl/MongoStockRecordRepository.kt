package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.impl

import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Avg
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
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
import systems.ajax.malov.stockanalyzer.stockrecord.application.port.out.StockRecordRepositoryOutPort
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.mapper.StockRecordMapper.toDomain
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.mapper.StockRecordMapper.toMongo
import java.math.BigDecimal
import java.util.Date
import org.springframework.data.mongodb.core.mapping.Document as SpringDocument

@Repository
class MongoStockRecordRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : StockRecordRepositoryOutPort {
    override fun insertAll(stockRecords: List<StockRecord>): Flux<StockRecord> {
        return reactiveMongoTemplate
            .insertAll(stockRecords.map { it.toMongo() })
            .map { it.toDomain() }
    }

    override fun findAllStockSymbols(): Flux<String> {
        return reactiveMongoTemplate.query(MongoStockRecord::class.java)
            .distinct(MongoStockRecord::symbol.name)
            .asType<String>()
            .all()
    }

    override fun findTopStockSymbolsWithStockRecords(
        quantity: Int,
        from: Date,
        to: Date,
    ): Mono<Map<String, List<StockRecord>>> {
        val maxChangeMono = getMaxBigDecimal(MongoStockRecord::change.name, from, to)
        val maxPercentChangeMono = getMaxBigDecimal(MongoStockRecord::percentChange.name, from, to)

        return Mono.zip(maxChangeMono, maxPercentChangeMono)
            .flatMap { (maxChange, maxPercentChange) ->
                fetchBestStockSymbolsWithStockRecords(from, to, maxChange, maxPercentChange, quantity)
                    .collectList()
            }
            .map { results ->
                results.flatMap { document ->
                    document.records.map { it.toDomain() }
                }
                    .groupBy { it.symbol ?: "Not provided" }
            }
            .map { mapWithAllDataRecords ->
                getOnlyMostRecentNDataRecords(mapWithAllDataRecords)
            }
            .defaultIfEmpty(linkedMapOf())
    }

    private fun fetchBestStockSymbolsWithStockRecords(
        from: Date,
        to: Date,
        maxChange: BigDecimal,
        maxPercentChange: BigDecimal,
        quantity: Int,
    ): Flux<ResultingClass> {
        val matchOperation: MatchOperation =
            Aggregation.match(
                Criteria.where(MongoStockRecord::dateOfRetrieval.name)
                    .gte(from)
                    .lt(to)
            )
        val groupOperation: GroupOperation = group(MongoStockRecord::symbol.name)
            .push("$\$ROOT").`as`("records")
        val projectOperation = getProjectWithAvgMaxValues(maxChange, maxPercentChange)
        val weightedPipeline = getWeightedPipeLine()

        val aggregation = Aggregation.newAggregation(
            matchOperation,
            groupOperation,
            projectOperation,
            weightedPipeline,
            Aggregation.sort(Sort.by("weight").descending()),
            Aggregation.limit(quantity.toLong())
        )

        return reactiveMongoTemplate.aggregate(
            aggregation,
            MongoStockRecord.COLLECTION_NAME,
            ResultingClass::class.java
        )
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

        val aggregation = Aggregation.newAggregation(
            matchDateRange,
            group()
                .max(field).`as`("max")
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, MongoStockRecord.COLLECTION_NAME, AggregatedBigDecimalResult::class.java)
            .next()
            .mapNotNull { it.max }
    }

    private fun getOnlyMostRecentNDataRecords(
        map: Map<String, List<StockRecord>>,
    ): Map<String, List<StockRecord>> {
        return map.entries.associateTo(LinkedHashMap()) { (key, value) ->
            key to value.sortedByDescending { stockRecord -> stockRecord.dateOfRetrieval }
                .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL)
        }
    }

    internal data class AggregatedBigDecimalResult(
        val max: BigDecimal?,
    )

    @SpringDocument
    internal data class ResultingClass(val records: List<MongoStockRecord>)

    companion object {
        private const val NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL = 5
        private const val WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS = 0.5
    }
}
