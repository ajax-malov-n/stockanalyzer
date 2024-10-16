package systems.ajax.malov.stockanalyzer.kafka.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice

@Component
class StockPriceNotificationProducer(
    private val kafkaNotificationStockPriceKafkaProducer: KafkaSender<String, NotificationStockPrice>,
) {
    fun sendNotificationStockPrice(notifications: List<NotificationStockPrice>): Mono<Unit> {
        return kafkaNotificationStockPriceKafkaProducer.send(
            notifications.map { it.buildKafkaNotification() }
                .toFlux()
        ).then(Unit.toMono())
    }

    private fun NotificationStockPrice.buildKafkaNotification(): SenderRecord<String, NotificationStockPrice, Any?> {
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                stockSymbolName,
                this
            ),
            null
        )
    }
}
