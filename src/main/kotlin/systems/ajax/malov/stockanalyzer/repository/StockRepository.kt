package systems.ajax.malov.stockanalyzer.repository

import systems.ajax.malov.stockanalyzer.entity.Stock

interface StockRepository {
    fun insertAll(stocks: List<Stock>): List<Stock>
    fun findTopNStockSymbolsWithStockData(n: Int): List<Pair<String, List<Stock>>>
    fun findAllStockSymbols(): List<String>
}
