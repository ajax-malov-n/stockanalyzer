package systems.ajax.malov.stockanalyzer.repository.impl

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Projections.excludeId
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Projections.include
import com.mongodb.client.model.Sorts
import org.bson.Document
import org.bson.types.Decimal128
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Avg
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import java.math.BigDecimal
import java.util.Date
import org.springframework.data.mongodb.core.mapping.Document as SDocument

@Repository
class MongoStockRecordRepository(
    private val mongoTemplate: MongoTemplate,
) : StockRecordRepository {
    override fun insertAll(mongoStockRecords: List<MongoStockRecord>): List<MongoStockRecord> {
        return mongoTemplate.insertAll(mongoStockRecords).toList()
    }

    override fun findAllStockSymbols(): List<String> {
        return mongoTemplate.getCollection(MongoStockRecord.COLLECTION_NAME)
            .distinct(MongoStockRecord::symbol.name, String::class.java)
            .toList()
    }

    override fun findTopNStockSymbolsWithStockRecords(
        n: Int,
        from: Date,
        to: Date,
    ): Map<String, List<MongoStockRecord>> {
        val maxChange = getMaxBigDecimal(MongoStockRecord::change.name, from, to)
        val maxPercentChange = getMaxBigDecimal(MongoStockRecord::percentChange.name, from, to)

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

        val results: List<ResultingClass> = mongoTemplate.aggregate(
            aggregation,
            MongoStockRecord.COLLECTION_NAME,
            ResultingClass::class.java
        ).mappedResults

        return getOnlyMostRecentNDataRecords(
            results.flatMap { it.records }
                .groupBy { it.symbol ?: "Not provided" }
        )
    }

    private fun getProjectWithAvgMaxValues(maxChange: BigDecimal?, maxPercentChange: BigDecimal?): ProjectionOperation {
        return Aggregation.project()
            .andInclude("records")
            .andExclude("_id")
            .and(getAvgOfRecordsArrayField(MongoStockRecord::change.name))
            .`as`("avgChange")
            .and(getAvgOfRecordsArrayField(MongoStockRecord::percentChange.name))
            .`as`("avgPercentChange")
            .and(aggregationExpression(maxChange))
            .`as`("maxChange")
            .and(aggregationExpression(maxPercentChange))
            .`as`("maxPercentChange")
            .and(aggregationExpression(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS.toBigDecimal()))
            .`as`("changeCoef")
            .and(aggregationExpression(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS.toBigDecimal()))
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

    private fun aggregationExpression(decimal: BigDecimal?): AggregationExpression {
        return AggregationExpression { _ ->
            Document(
                "\$toDecimal",
                decimal?.toString()
            )
        }
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
    ): BigDecimal? {
        val collection: MongoCollection<Document> = mongoTemplate
            .getCollection(MongoStockRecord.COLLECTION_NAME)

        return collection
            .find()
            .filter(gte(MongoStockRecord::dateOfRetrieval.name, from))
            .filter(lt(MongoStockRecord::dateOfRetrieval.name, to))
            .projection(
                fields(
                    include(field),
                    excludeId()
                )
            )
            .sort((Sorts.descending(field)))
            .limit(1)
            .firstOrNull()?.let {
                (it[field] as? Decimal128)?.bigDecimalValue()
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

    @SDocument
    internal data class ResultingClass(val records: List<MongoStockRecord>)

    companion object {
        private const val NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL = 5
        private const val WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS = 0.5
    }
}
