package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.entity.StockRecord

interface StockRecordClientApi {
    fun getAllStockRecords(): List<StockRecord>
}
