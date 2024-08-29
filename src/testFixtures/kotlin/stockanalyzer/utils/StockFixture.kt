package stockanalyzer.utils

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.entity.StockRecord
import systems.ajax.malov.stockanalyzer.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"

    fun testDate(): Instant = Instant.ofEpochMilli(111111111)

    fun unsavedStockRecord() = StockRecord(
        symbol = TEST_STOCK_SYMBOL,
        openPrice = BigDecimal("1.0"),
        highPrice = BigDecimal("1.0"),
        lowPrice = BigDecimal("1.0"),
        currentPrice = BigDecimal("1.0"),
        previousClosePrice = BigDecimal("1.0"),
        change = BigDecimal("1.0"),
        percentChange = BigDecimal("1.0"),
        dateOfRetrieval = testDate()
    )

    fun savedStockRecord() = StockRecord(
        id = UUID.fromString("dc0aaf1b-7917-4ae8-b3e0-9539466e54a4"),
        symbol = TEST_STOCK_SYMBOL,
        openPrice = BigDecimal("1.0"),
        highPrice = BigDecimal("1.0"),
        lowPrice = BigDecimal("1.0"),
        currentPrice = BigDecimal("1.0"),
        previousClosePrice = BigDecimal("1.0"),
        change = BigDecimal("1.0"),
        percentChange = BigDecimal("1.0"),
        dateOfRetrieval = testDate()
    )

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

    fun aggregatedStockRecordResponseDto() = AggregatedStockRecordResponseDto(
        notAggregatedResponseForFiveBestStockSymbolsWithStockRecords().map {
            AggregatedStockRecordItemResponseDto(
                it.key,
                it.value.map { stock -> stock.toShortStockRecordResponseDto() })
        }
    )
}
