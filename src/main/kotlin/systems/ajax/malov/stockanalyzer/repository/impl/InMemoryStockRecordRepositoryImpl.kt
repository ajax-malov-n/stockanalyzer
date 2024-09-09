package systems.ajax.malov.stockanalyzer.repository.impl

import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Repository
import systems.ajax.malov.stockanalyzer.entity.StockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import kotlin.reflect.KProperty1


@Repository
class InMemoryStockRecordRepositoryImpl : StockRecordRepository {
    private val db: HashMap<UUID, StockRecord> = HashMap()

    override fun insertAll(stockRecords: List<StockRecord>): List<StockRecord> {
        stockRecords.forEach {
            val id = UUID.randomUUID()
            it.id = id
            db[id] = it
        }
        return stockRecords
    }

    @Suppress("UnsafeCallOnNullableType")
    override fun findTopNStockSymbolsWithStockRecords(n: Int): Map<String, List<StockRecord>> {
        if (db.isEmpty()) return emptyMap()

        val maxPercentChange = getMaxBigDecimal(StockRecord::percentChange)
        val maxChange = getMaxBigDecimal(StockRecord::change)

        val timeOfRequest = Instant.now()

        return db.values
            .asSequence()
            .filterNot { it.symbol == null }
            .filter { stockRecord -> isStockInValidDateRange(stockRecord, timeOfRequest) }
            .groupBy { it.symbol!! }
            .toList()
            .asSequence()
            .sortedByDescending { (_, stockRecords) -> calculateWeight(stockRecords, maxChange, maxPercentChange) }
            .take(n)
            .associate { (symbol, stockRecords) ->
                symbol to stockRecords.distinctBy { it.currentPrice }
                    .sortedByDescending { it.dateOfRetrieval }
                    .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL)
            }
    }

    override fun findAllStockSymbols(): Set<String> {
        return db.values
            .mapNotNull { it.symbol }
            .toSet()
    }

    private fun calculateWeight(
        stockRecordList: List<StockRecord>,
        maxChange: BigDecimal,
        maxPercentChange: BigDecimal,
    ): BigDecimal {
        val avgChange = getAvgOfBigDecimals(stockRecordList, StockRecord::change)
        val avgPercentChange = getAvgOfBigDecimals(stockRecordList, StockRecord::percentChange)

        val priceChangeCoefficient =
            (avgChange / maxChange) * BigDecimal.valueOf(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS)
        val pricePercentChangeCoefficient =
            (avgPercentChange / maxPercentChange) * BigDecimal.valueOf(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS)
        return priceChangeCoefficient + pricePercentChangeCoefficient
    }

    private fun isStockInValidDateRange(stockRecord: StockRecord, timeOfRequest: Instant): Boolean {
        return stockRecord.dateOfRetrieval
            ?.isBefore(
                timeOfRequest.plus(Duration.ofHours(1))
            ) == true
    }

    private fun getMaxBigDecimal(property: KProperty1<StockRecord, BigDecimal?>): BigDecimal {
        return db.values
            .mapNotNull(property)
            .max()
    }

    private fun getAvgOfBigDecimals(
        stockRecordList: List<StockRecord>,
        property: KProperty1<StockRecord, BigDecimal?>,
    ): BigDecimal {
        return stockRecordList
            .mapNotNull(property)
            .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc + bigDecimal } /
                BigDecimal.valueOf(stockRecordList.size.toLong())
    }


    private companion object {
        const val NUMBER_OF_HISTORY_RECORDS_PER_STOCK_SYMBOL = 5
        const val WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS = 0.5
    }
}
