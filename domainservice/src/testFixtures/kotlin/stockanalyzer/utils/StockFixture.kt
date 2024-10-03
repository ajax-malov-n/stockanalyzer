package stockanalyzer.utils

import org.bson.types.ObjectId
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    val ID = ObjectId.get()
    val testDate = Clock.fixed(Instant.now(), ZoneId.of("UTC")).instant()

    fun testDate(): Instant = Clock.fixed(Instant.now(), ZoneId.of("UTC")).instant()

    fun unsavedStockRecord() = MongoStockRecord(
        id = null,
        symbol = TEST_STOCK_SYMBOL,
        openPrice = BigDecimal("1.0"),
        highPrice = BigDecimal("1.0"),
        lowPrice = BigDecimal("1.0"),
        currentPrice = BigDecimal("1.0"),
        previousClosePrice = BigDecimal("1.0"),
        change = BigDecimal("1.0"),
        percentChange = BigDecimal("1.0"),
        dateOfRetrieval = testDate
    )

    fun savedStockRecord(): MongoStockRecord {
        return MongoStockRecord(
            id = ID,
            symbol = TEST_STOCK_SYMBOL,
            openPrice = BigDecimal("1.0"),
            highPrice = BigDecimal("1.0"),
            lowPrice = BigDecimal("1.0"),
            currentPrice = BigDecimal("1.0"),
            previousClosePrice = BigDecimal("1.0"),
            change = BigDecimal("1.0"),
            percentChange = BigDecimal("1.0"),
            dateOfRetrieval = testDate
        )
    }

    fun firstPlaceStockRecord() = savedStockRecord().copy(
        symbol = "AAPL",
        change = BigDecimal("2.0"),
        percentChange = BigDecimal("0.3"),
        dateOfRetrieval = Instant.now()
    )

    fun alsoFirstPlaceStockRecord() = savedStockRecord().copy(
        symbol = "AAPL",
        change = BigDecimal("2.5"),
        percentChange = BigDecimal("0.3"),
        dateOfRetrieval = Instant.now()
    )

    fun secondPlaceStockRecord() = savedStockRecord().copy(
        symbol = "SSSK",
        change = BigDecimal("1.0"),
        percentChange = BigDecimal("0.2"),
        dateOfRetrieval = Instant.now()
    )

    fun notAggregatedResponseForFiveBestStockSymbolsWithStockRecords() =
        mapOf(TEST_STOCK_SYMBOL to listOf(savedStockRecord()))
}
