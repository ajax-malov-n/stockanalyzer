package systems.ajax.malov.stockanalyzer.kafka.producer

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice

@Component
class StockPriceNotificationProducer(
    private val kafkaPublisher: KafkaPublisher,
) {
    fun sendNotificationStockPrice(notifications: List<NotificationStockPrice>): Mono<Unit> {
        return notifications.toFlux()
            .flatMap {
                kafkaPublisher.publish(
                    KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                    it.stockSymbolName,
                    it,
                )
            }.then(Unit.toMono())
    }
}
