package systems.ajax.malov.stockanalyzer.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordAnalyzerService {
    fun getNBestStockSymbolsWithStockRecords(n: Int): Mono<LinkedHashMap<String, List<MongoStockRecord>>>
    fun getAllManageableStocksSymbols(): Flux<String>
}
