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
    private val kafkaNotificationStockPriceSender: KafkaSender<String, ByteArray>,
) {
    fun sendNotificationStockPrice(notifications: List<NotificationStockPrice>): Mono<Unit> {
        return kafkaNotificationStockPriceSender.send(
            notifications.map { it.buildKafkaNotification() }
                .toFlux()
        ).then(Unit.toMono())
    }

    private fun NotificationStockPrice.buildKafkaNotification(): SenderRecord<String, ByteArray, Any?> {
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                stockSymbolName,
                toByteArray()
            ),
            null
        )
    }
}
