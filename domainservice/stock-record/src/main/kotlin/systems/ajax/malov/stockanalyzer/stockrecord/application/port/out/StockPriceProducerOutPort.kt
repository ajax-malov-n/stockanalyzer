package systems.ajax.malov.stockanalyzer.stockrecord.application.port.out

import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord

interface StockPriceProducerOutPort {
    fun sendStockPrice(mongoStockRecords: List<StockRecord>): Mono<Unit>
}
