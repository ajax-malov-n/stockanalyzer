package systems.ajax.malov.stockanalyzer.repository

import systems.ajax.malov.stockanalyzer.entity.Stock

interface StockRepository {
    fun insertAll(stocks: List<Stock>): MutableCollection<Stock>
    fun findFiveBestStocksToBuy() : List<Pair<String?, List<Stock>>>
    fun getAllStockSymbols(): List<String?>
}