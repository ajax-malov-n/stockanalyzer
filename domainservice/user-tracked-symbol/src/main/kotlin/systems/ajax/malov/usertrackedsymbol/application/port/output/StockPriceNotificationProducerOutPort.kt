package systems.ajax.malov.usertrackedsymbol.application.port.output

import reactor.core.publisher.Mono
import systems.ajax.malov.usertrackedsymbol.domain.NotificationStockPrice

interface StockPriceNotificationProducerOutPort {
    fun sendNotificationStockPrice(notifications: List<NotificationStockPrice>): Mono<Unit>
}
