package systems.ajax.malov.stockanalyzer.dto

import systems.ajax.malov.stockanalyzer.entity.Stock

data class AggregatedStockItemResponse(val stockSymbol: String, val data: List<ShortStockResponse>)
{
    companion object {
        fun fromStocks(
            symbol: String,
            src: List<Stock>
        ) = AggregatedStockItemResponse(
            stockSymbol = symbol,
            data = src.map { ShortStockResponse.fromStock(it) })
    }
}