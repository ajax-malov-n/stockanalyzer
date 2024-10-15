package systems.ajax.malov.stockanalyzer.kafka.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import java.time.Duration

@Component
class StockPriceNotificationProducer(
    private val kafkaNotificationStockPriceKafkaProducer: KafkaSender<String, NotificationStockPrice>,
) {
    fun sendNotificationStockPrice(notificationStockPrices: List<NotificationStockPrice>): Mono<Unit> {
        return notificationStockPrices
            .toFlux()
            .buffer(Duration.ofSeconds(1))
            .flatMap {
                kafkaNotificationStockPriceKafkaProducer.send(
                    it.map { stockPrice -> buildKafkaNotificationStockPriceMessage(stockPrice) }
                        .toFlux()
                )
            }
            .then(Unit.toMono())
    }

    private fun buildKafkaNotificationStockPriceMessage(
        notificationStockPriceDto: NotificationStockPrice,
    ): SenderRecord<String, NotificationStockPrice, Any?> {
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                notificationStockPriceDto.stockSymbolName,
                notificationStockPriceDto
            ),
            null
        )
    }
}
