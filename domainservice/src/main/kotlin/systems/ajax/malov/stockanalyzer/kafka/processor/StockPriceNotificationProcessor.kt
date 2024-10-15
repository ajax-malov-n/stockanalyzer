package systems.ajax.malov.stockanalyzer.kafka.processor

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceNotificationProducer
import systems.ajax.malov.stockanalyzer.service.NotificationStockPriceService

@Component
class StockPriceNotificationProcessor(
    private val kafkaStockPriceConsumer: KafkaReceiver<String, StockPrice>,
    private val kafkaNotificationStockPriceSender: StockPriceNotificationProducer,
    private val notificationStockPriceService: NotificationStockPriceService,
) {
    @PostConstruct
    fun subscribeToEvents() {
        kafkaStockPriceConsumer.receiveAutoAck()
            .flatMap { stockPriceConsumerRecords ->
                stockPriceConsumerRecords.flatMap { stockPriceConsumerRecord ->
                    notificationStockPriceService.createUsersNotifications(stockPriceConsumerRecord.value())
                }
            }.flatMap { notifications ->
                kafkaNotificationStockPriceSender.sendNotificationStockPrice(notifications.map { it._1 })
                    .thenMany(notifications.toMono())
            }
            .flatMap { notifications ->
                notificationStockPriceService
                    .deleteUserTrackedSymbols(notifications.map { it._2 })
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
