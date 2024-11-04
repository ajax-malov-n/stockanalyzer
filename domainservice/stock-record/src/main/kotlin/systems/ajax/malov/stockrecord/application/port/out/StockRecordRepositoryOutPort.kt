package systems.ajax.malov.stockrecord.application.port.out

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockrecord.domain.StockRecord
import java.util.Date

interface StockRecordRepositoryOutPort {
    fun insertAll(stockRecords: List<StockRecord>): Flux<StockRecord>

    fun findTopStockSymbolsWithStockRecords(
        quantity: Int,
        from: Date,
        to: Date,
    ): Mono<Map<String, List<StockRecord>>>

    fun findAllStockSymbols(): Flux<String>
}
