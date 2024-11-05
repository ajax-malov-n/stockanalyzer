package systems.ajax.malov.stockanalyzer.stockrecord.application.port.out

import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockPrice

interface StockMessageOutPort {
    fun publishStockPrice(
        stockPrice: StockPrice,
    ): Mono<Unit>
}
