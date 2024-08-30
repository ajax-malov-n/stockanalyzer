package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.entity.StockRecord

interface StockRecordAnalyzerService {
    fun getFiveBestStockSymbolsWithStockRecords(): Map<String, List<StockRecord>>
    fun getAllManageableStocksSymbols(): Set<String>
}
