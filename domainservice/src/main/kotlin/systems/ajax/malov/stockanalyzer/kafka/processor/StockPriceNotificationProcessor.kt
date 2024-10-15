package systems.ajax.malov.stockanalyzer.kafka.processor

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
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
            }.flatMap {
                kafkaNotificationStockPriceSender.sendNotificationStockPrice(it)
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
