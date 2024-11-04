package systems.ajax.malov.stockrecord.application.port.out

import reactor.core.publisher.Mono
import systems.ajax.malov.stockrecord.domain.StockRecord

interface StockPriceProducerOutPort {
    fun sendStockPrice(mongoStockRecords: List<StockRecord>): Mono<Unit>
}
