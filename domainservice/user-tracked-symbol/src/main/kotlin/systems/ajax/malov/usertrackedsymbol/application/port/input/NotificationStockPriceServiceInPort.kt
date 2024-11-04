package systems.ajax.malov.usertrackedsymbol.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import systems.ajax.malov.stockrecord.domain.StockPrice
import systems.ajax.malov.usertrackedsymbol.domain.NotificationStockPrice

interface NotificationStockPriceServiceInPort {
    fun createUsersNotifications(stockPrice: StockPrice): Flux<Tuple2<NotificationStockPrice, String>>
    fun deleteUserTrackedSymbols(ids: List<String>): Mono<Unit>
}
