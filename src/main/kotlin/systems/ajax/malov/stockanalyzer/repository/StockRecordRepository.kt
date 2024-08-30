package systems.ajax.malov.stockanalyzer.repository

import systems.ajax.malov.stockanalyzer.entity.StockRecord

interface StockRecordRepository {
    fun insertAll(stockRecords: List<StockRecord>): List<StockRecord>
    fun findTopNStockSymbolsWithStockRecords(n: Int): Map<String, List<StockRecord>>
    fun findAllStockSymbols(): Set<String>
}
