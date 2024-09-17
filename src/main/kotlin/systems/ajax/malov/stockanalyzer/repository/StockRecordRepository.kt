package systems.ajax.malov.stockanalyzer.repository

import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.util.Date

interface StockRecordRepository {
    fun insertAll(mongoStockRecords: List<MongoStockRecord>): List<MongoStockRecord>
    fun findTopNStockSymbolsWithStockRecords(n: Int, from: Date, to: Date): Map<String, List<MongoStockRecord>>
    fun findAllStockSymbols(): List<String>
}
