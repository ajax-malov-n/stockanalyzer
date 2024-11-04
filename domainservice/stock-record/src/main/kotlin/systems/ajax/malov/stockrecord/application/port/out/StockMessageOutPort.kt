package systems.ajax.malov.stockrecord.application.port.out

import reactor.core.publisher.Mono
import systems.ajax.malov.stockrecord.domain.StockPrice

interface StockMessageOutPort {
    fun publishStockPrice(
        stockPrice: StockPrice,
    ): Mono<Unit>
}
