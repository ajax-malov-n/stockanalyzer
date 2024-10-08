package systems.ajax.malov.stockanalyzer.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordAnalyzerService {
    fun getBestStockSymbolsWithStockRecords(quantity: Int): Mono<Map<String, List<MongoStockRecord>>>
    fun getAllManageableStocksSymbols(): Flux<String>
}
