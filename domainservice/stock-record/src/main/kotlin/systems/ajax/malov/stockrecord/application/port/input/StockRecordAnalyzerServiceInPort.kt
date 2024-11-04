package systems.ajax.malov.stockrecord.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockrecord.domain.StockRecord

interface StockRecordAnalyzerServiceInPort {
    fun getBestStockSymbolsWithStockRecords(quantity: Int): Mono<Map<String, List<StockRecord>>>
    fun getAllManageableStocksSymbols(): Flux<String>
}
