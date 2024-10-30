package systems.ajax.malov.stockanalyzer.kafka.processor

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceNotificationProducer
import systems.ajax.malov.stockanalyzer.service.NotificationStockPriceService

@Component
class StockPriceNotificationProcessor(
    private val kafkaStockPriceReceiver: KafkaReceiver<String, ByteArray>,
    private val kafkaNotificationStockPriceSender: StockPriceNotificationProducer,
    private val notificationStockPriceService: NotificationStockPriceService,
) {
    @PostConstruct
    fun subscribeToEvents() {
        kafkaStockPriceReceiver.receiveBatch()
            .flatMap { stockPriceConsumerRecords ->
                stockPriceConsumerRecords.flatMap { record ->
                    handle(StockPrice.parseFrom(record.value()))
                        .doFinally { record.receiverOffset().acknowledge() }
                }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun handle(stockPrice: StockPrice): Mono<Unit> {
        return notificationStockPriceService
            .createUsersNotifications(stockPrice)
            .collectList()
            .flatMapMany {
                kafkaNotificationStockPriceSender.sendNotificationStockPrice(it.map { (stockPrice, _) -> stockPrice })
                    .thenMany(it.toMono())
            }.flatMap { notifications ->
                notificationStockPriceService
                    .deleteUserTrackedSymbols(notifications.map { (_, userTrackedStockId) -> userTrackedStockId })
            }
            .then(Unit.toMono())
    }
}
