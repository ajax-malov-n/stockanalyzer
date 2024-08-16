package systems.ajax.malov.stockanalyzer.utils

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockItemResponse
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockResponse
import systems.ajax.malov.stockanalyzer.entity.Stock
import java.util.*

const val TEST_STOCK_SYMBOL = "AAPL"

val unsavedStock = Stock(
    symbol = TEST_STOCK_SYMBOL,
    openPrice = 1.0f,
    highPrice = 0.001f,
    lowPrice = 1.0f,
    currentPrice = 1.0f,
    previousClosePrice = 1.0f,
    change = 1.0f,
    percentChange = 1.0f,
    dateOfRetrieval = null
)

val savedStock = Stock(
    id = UUID.randomUUID(),
    symbol = TEST_STOCK_SYMBOL,
    openPrice = 1.0f,
    highPrice = 0.001f,
    lowPrice = 1.0f,
    currentPrice = 1.0f,
    previousClosePrice = 1.0f,
    change = 1.0f,
    percentChange = 1.0f,
    dateOfRetrieval = null
)

val aggregatedStockResponse = listOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock))).map {
    AggregatedStockResponse(AggregatedStockItemResponse.fromStocks(it.first, it.second))
}