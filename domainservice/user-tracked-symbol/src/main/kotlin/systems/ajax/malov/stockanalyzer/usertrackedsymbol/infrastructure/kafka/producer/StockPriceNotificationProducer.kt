package systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.kafka.producer

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.output.StockPriceNotificationProducerOutPort
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain.NotificationStockPrice
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.kafka.mapper.NotificationStockPriceMapper.toNotificationStockPrice

@Component
class StockPriceNotificationProducer(
    private val kafkaPublisher: KafkaPublisher,
) : StockPriceNotificationProducerOutPort {
    override fun sendNotificationStockPrice(notifications: List<NotificationStockPrice>): Mono<Unit> {
        return notifications.toFlux()
            .map { it.toNotificationStockPrice() }
            .flatMap {
                kafkaPublisher.publish(
                    KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                    it.stockSymbolName,
                    it,
                )
            }.then(Unit.toMono())
    }
}
