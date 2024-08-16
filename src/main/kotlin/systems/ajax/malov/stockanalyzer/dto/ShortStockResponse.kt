package systems.ajax.malov.stockanalyzer.dto

import systems.ajax.malov.stockanalyzer.entity.Stock

data class ShortStockResponse (
    val openPrice: Float?,
    val highPrice: Float?,
    val lowPrice: Float?,
    val currentPrice: Float?,
) {
    companion object {
        fun fromStock(
            src: Stock
        ) = ShortStockResponse(
            openPrice = src.openPrice,
            highPrice = src.highPrice,
            lowPrice = src.lowPrice,
            currentPrice = src.currentPrice)
    }
}