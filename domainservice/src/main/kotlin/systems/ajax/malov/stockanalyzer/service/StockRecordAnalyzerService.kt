package systems.ajax.malov.stockanalyzer.service

import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordAnalyzerService {
    fun getFiveBestStockSymbolsWithStockRecords(n: Int): Mono<Map<String, List<MongoStockRecord>>>
    fun getAllManageableStocksSymbols(): Mono<List<String>>
}
