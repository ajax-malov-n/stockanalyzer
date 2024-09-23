package systems.ajax.malov.stockanalyzer.repository.impl

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Projections.excludeId
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Projections.include
import com.mongodb.client.model.Sorts
import org.bson.Document
import org.bson.types.Decimal128
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
import reactor.kotlin.core.publisher.toMono
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

    override fun findAllStockSymbols(): Flux<String> {
        return reactiveMongoTemplate.query(MongoStockRecord::class.java)
            .distinct(MongoStockRecord::symbol.name)
            .asType<String>()
            .all()
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
                val maxChange = maxValues.t1
                val maxPercentChange = maxValues.t2

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

                reactiveMongoTemplate.aggregate(
                    aggregation,
                    MongoStockRecord.COLLECTION_NAME,
                    ResultingClass::class.java
                ).collectList()
            }
            .map { results ->
                getOnlyMostRecentNDataRecords(
                    results.flatMap { it.records }
                        .groupBy { it.symbol ?: "Not provided" }
                )
            }
    }

    private fun getProjectWithAvgMaxValues(
        maxChange: BigDecimal?,
        maxPercentChange: BigDecimal?,
    ): ProjectionOperation {
        return Aggregation.project()
            .andInclude("records")
            .andExclude("_id")
            .and(getAvgOfRecordsArrayField(MongoStockRecord::change.name))
            .`as`("avgChange")
            .and(getAvgOfRecordsArrayField(MongoStockRecord::percentChange.name))
            .`as`("avgPercentChange")
            .and(toDecimal(maxChange?.toString().orEmpty()))
            .`as`("maxChange")
            .and(toDecimal(maxPercentChange?.toString().orEmpty()))
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
    ): Mono<BigDecimal?> {
        return reactiveMongoTemplate
            .getCollection(MongoStockRecord.COLLECTION_NAME)
            .flatMap { collection ->
                collection.find(
                    and(
                        gte(MongoStockRecord::dateOfRetrieval.name, from),
                        lt(MongoStockRecord::dateOfRetrieval.name, to)
                    )
                )
                    .projection(
                        fields(
                            include(field),
                            excludeId()
                        )
                    )
                    .sort(Sorts.descending(field))
                    .limit(1)
                    .toMono()
            }
            .flatMap { document ->
                Mono.justOrEmpty(
                    (document?.get(field) as? Decimal128)?.bigDecimalValue()
                )
            }
    }

    private fun getOnlyMostRecentNDataRecords(
        map: Map<String, List<MongoStockRecord>>,
    ): Map<String, List<MongoStockRecord>> {
        return map.mapValues {
            it.value.sortedByDescending { stockRecord -> stockRecord.dateOfRetrieval }
                .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL)
        }
    }

    @SpringDocument
    internal data class ResultingClass(val records: List<MongoStockRecord>)

    companion object {
        private const val NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL = 5
        private const val WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS = 0.5
    }
}
