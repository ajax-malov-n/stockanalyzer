package utils

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockResponseDto
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.mapper.ShortStockResponseDtoMapper.toShortStockResponseDto
import java.math.BigDecimal
import java.time.Instant
import java.util.*


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"

    fun testDate(): Instant = Instant.ofEpochMilli(111111111)

    fun unsavedStock() = Stock(
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

    fun savedStock() = Stock(
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

    fun firstPlaceStock() = savedStock().copy(
        symbol = "AAPL",
        change = BigDecimal("2.0"),
        percentChange = BigDecimal("0.3"),
        dateOfRetrieval = Instant.now()
    )

    fun alsoFirstPlaceStock() = savedStock().copy(
        symbol = "AAPL",
        change = BigDecimal("2.5"),
        percentChange = BigDecimal("0.3"),
        dateOfRetrieval = Instant.now()
    )

    fun secondPlaceStock() = savedStock().copy(
        symbol = "SSSK",
        change = BigDecimal("1.0"),
        percentChange = BigDecimal("0.2"),
        dateOfRetrieval = Instant.now()
    )

    fun notAggregatedResponseForFiveBestStocks() = listOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock())))

    fun aggregatedStockResponseDto() = AggregatedStockResponseDto(
        notAggregatedResponseForFiveBestStocks().map {
            AggregatedStockItemResponseDto(it.first, it.second.map { stock -> stock.toShortStockResponseDto() })
        }
    )
}
