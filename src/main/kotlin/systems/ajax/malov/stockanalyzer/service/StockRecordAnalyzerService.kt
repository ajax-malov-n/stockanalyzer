package systems.ajax.malov.stockanalyzer.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordAnalyzerService {
    fun getFiveBestStockSymbolsWithStockRecords(): Mono<Map<String, List<MongoStockRecord>>>
    fun getAllManageableStocksSymbols(): Flux<String>
}
