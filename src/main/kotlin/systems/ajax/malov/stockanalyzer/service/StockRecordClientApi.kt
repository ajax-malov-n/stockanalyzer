package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordClientApi {
    fun getAllStockRecords(): List<MongoStockRecord>
}
