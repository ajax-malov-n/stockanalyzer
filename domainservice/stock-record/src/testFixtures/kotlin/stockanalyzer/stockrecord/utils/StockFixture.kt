package stockanalyzer.stockrecord.utils

import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.entity.MongoStockRecord
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    val testDate: Instant = Clock.fixed(Instant.now(), ZoneId.of("UTC")).instant()

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

    fun domainStockRecord() = StockRecord(
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

    fun notAggregatedResponseForBestStockSymbolsWithStockRecords() =
        linkedMapOf(TEST_STOCK_SYMBOL to listOf(domainStockRecord()))
}
