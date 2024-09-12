package systems.ajax.malov.stockanalyzer.repository

import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordRepository {
    fun insertAll(mongoStockRecords: List<MongoStockRecord>): List<MongoStockRecord>
    fun findTopNStockSymbolsWithStockRecords(n: Int): Map<String, List<MongoStockRecord>>
    fun findAllStockSymbols(): List<String>
}
