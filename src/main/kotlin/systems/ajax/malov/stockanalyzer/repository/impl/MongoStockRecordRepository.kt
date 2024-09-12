package systems.ajax.malov.stockanalyzer.repository.impl

import com.mongodb.client.AggregateIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Accumulators.push
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Aggregates.group
import com.mongodb.client.model.Aggregates.match
import com.mongodb.client.model.Aggregates.project
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Indexes.descending
import com.mongodb.client.model.Projections.computed
import com.mongodb.client.model.Projections.exclude
import com.mongodb.client.model.Projections.excludeId
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Projections.include
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.mql.MqlArray
import com.mongodb.client.model.mql.MqlDocument
import com.mongodb.client.model.mql.MqlNumber
import com.mongodb.client.model.mql.MqlValues.current
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.Decimal128
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository


@Repository
class MongoStockRecordRepository(private val mongoTemplate: MongoTemplate) : StockRecordRepository {
    override fun insertAll(mongoStockRecords: List<MongoStockRecord>): List<MongoStockRecord> {
        return mongoTemplate.insertAll(mongoStockRecords)
            .toList()
    }

    override fun findTopNStockSymbolsWithStockRecords(n: Int): Map<String, List<MongoStockRecord>> {
        val collection: MongoCollection<Document> = mongoTemplate
            .getCollection(MongoStockRecord.COLLECTION_NAME)

        val pipeline: List<Bson> = listOf(
            Date.from(
                Instant.now()
                    .minus(1, ChronoUnit.HOURS)
            ).let {
                match(gte(MongoStockRecord::dateOfRetrieval.name, it))
            },
            group(
                current().getString(MongoStockRecord::symbol.name),
                push("records", "\$\$ROOT")
            ),
            getProjectWithAvgMaxValues(),
            getWeightedPipeLine(),
            Aggregates.sort(descending("weight")),
            Aggregates.limit(n)
        )

        val results: AggregateIterable<Document> = collection.aggregate(pipeline)

        return getOnlyMostRecentNDataRecords(
            convertResultsToList(results.toList())
        )
    }

    override fun findAllStockSymbols(): List<String> {
        return mongoTemplate.getCollection(MongoStockRecord.COLLECTION_NAME)
            .distinct(MongoStockRecord::symbol.name, String::class.java)
            .toList()
    }

    private fun getMaxBigDecimal(field: String): BigDecimal {
        val collection: MongoCollection<Document> = mongoTemplate
            .getCollection(MongoStockRecord.COLLECTION_NAME)

        return collection
            .find()
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
            } ?: BigDecimal.ONE
    }

    private fun getProjectWithAvgMaxValues(): Bson {
        val maxChange = getMaxBigDecimal(MongoStockRecord::change.name)
        val maxPercentChange = getMaxBigDecimal(MongoStockRecord::percentChange.name)
        val records = current().getArray<MqlDocument>("records")
        return project(
            fields(
                exclude("_id"),
                include("records"),
                computed(
                    "avgChange",
                    gradeAverage(records, MongoStockRecord::change.name)
                ),
                computed(
                    "avgPercentChange",
                    gradeAverage(records, MongoStockRecord::percentChange.name)
                ),
                computed(
                    "maxChange",
                    Document("\$toDecimal", maxChange.toString())
                ),
                computed(
                    "maxPercentChange",
                    Document("\$toDecimal", maxPercentChange.toString())
                ),
                computed(
                    "changeCoef",
                    Document("\$toDecimal", WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS.toString())
                ),
                computed(
                    "percentChangeCoef",
                    Document("\$toDecimal", WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS.toString())
                ),
            )
        )
    }

    private fun getWeightedPipeLine(): Bson {
        val firstCoef = current().getNumber("avgChange")
            .divide(current().getNumber("maxChange"))
            .multiply(current().getNumber("changeCoef"))
        val secondCoef = current().getNumber("avgPercentChange")
            .divide(current().getNumber("maxPercentChange"))
            .multiply(current().getNumber("percentChangeCoef"))

        val weight = firstCoef.add(secondCoef)

        val projectionWithWeightCoef = project(
            fields(
                include("records"),
                computed(
                    "weight", weight
                )
            )
        )
        return projectionWithWeightCoef
    }

    private fun gradeAverage(students: MqlArray<MqlDocument>, fieldName: String): MqlNumber {
        val sum = students
            .sum { student -> student.getInteger(fieldName) }
        return sum.divide(students.size())
    }

    private fun getOnlyMostRecentNDataRecords(map: Map<String, MutableList<MongoStockRecord>>)
            : Map<String, List<MongoStockRecord>> {
        return map.mapValues {
            it.value.sortedByDescending { stockRecord -> stockRecord.dateOfRetrieval }
                .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL)
        }
    }

    private fun convertResultsToList(results: List<Document>): Map<String, MutableList<MongoStockRecord>> {
        val stockRecordsMap = mutableMapOf<String, MutableList<MongoStockRecord>>()
        results.forEach { document ->
            val recordsRaw = document.get("records", ArrayList::class.java)
            val records = recordsRaw
                .takeIf { recordsRaw is List<*> }
                ?.filterIsInstance<Document>()
                .orEmpty()

            records.forEach { recordDoc ->
                val stockRecord = convertDocumentToMongoStockRecord(recordDoc)
                stockRecord.symbol?.let { stockRecordsMap.computeIfAbsent(it) { mutableListOf() }.add(stockRecord) }
            }
        }

        return stockRecordsMap.toMap()
    }

    private fun convertDocumentToMongoStockRecord(document: Document): MongoStockRecord {
        return MongoStockRecord(
            id = document.getObjectId("_id"),
            symbol = document.getString("symbol"),
            openPrice = (document["openPrice"] as? Decimal128)?.bigDecimalValue(),
            highPrice = (document["highPrice"] as? Decimal128)?.bigDecimalValue(),
            lowPrice = (document["lowPrice"] as? Decimal128)?.bigDecimalValue(),
            currentPrice = (document["currentPrice"] as? Decimal128)?.bigDecimalValue(),
            previousClosePrice = (document["previousClosePrice"] as? Decimal128)?.bigDecimalValue(),
            change = (document["change"] as? Decimal128)?.bigDecimalValue(),
            percentChange = (document["percentChange"] as? Decimal128)?.bigDecimalValue(),
            dateOfRetrieval = (document["dateOfRetrieval"] as? Date)?.toInstant()
        )
    }

    companion object {
        private const val NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL = 5
        private const val WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS = 0.5
    }
}
