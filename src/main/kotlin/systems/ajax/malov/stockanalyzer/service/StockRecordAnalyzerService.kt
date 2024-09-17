package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordAnalyzerService {
    fun getFiveBestStockSymbolsWithStockRecords(): Map<String, List<MongoStockRecord>>
    fun getAllManageableStocksSymbols(): List<String>
}
