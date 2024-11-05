package systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.output

import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain.NotificationStockPrice

interface StockPriceNotificationProducerOutPort {
    fun sendNotificationStockPrice(notifications: List<NotificationStockPrice>): Mono<Unit>
}
