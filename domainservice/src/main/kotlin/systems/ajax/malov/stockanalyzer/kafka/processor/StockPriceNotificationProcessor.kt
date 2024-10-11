package systems.ajax.malov.stockanalyzer.kafka.processor

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toFlux
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertBigDecimalProtoToBigDecimal
import systems.ajax.malov.stockanalyzer.repository.UserTrackedSymbolRepository

@Component
class StockPriceNotificationProcessor(
    private val kafkaStockPriceConsumer: KafkaReceiver<String, StockPrice>,
    private val kafkaNotificationStockPriceSender: KafkaSender<String, NotificationStockPrice>,
    private val userTrackedSymbolRepository: UserTrackedSymbolRepository,
) {
    @PostConstruct
    fun subscribeToEvents() {
        kafkaStockPriceConsumer.receiveAutoAck()
            .flatMap { stockPriceConsumerRecords ->
                stockPriceConsumerRecords.flatMap { stockPriceConsumerRecord ->
                    createUsersNotifications(stockPriceConsumerRecord)
                }
            }.flatMap {
                kafkaNotificationStockPriceSender.send(it.toFlux())
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun createUsersNotifications(stockPriceConsumerRecord: ConsumerRecord<String, StockPrice>) =
        findUsersToNotify(stockPriceConsumerRecord).collectList()
            .flatMap { userTrackedSymbols ->
                buildKafkaNotifications(userTrackedSymbols, stockPriceConsumerRecord)
            }

    private fun buildKafkaNotifications(
        userTrackedSymbols: MutableList<MongoUserTrackedSymbol>,
        stockPriceConsumerRecord: ConsumerRecord<String, StockPrice>,
    ) = userTrackedSymbolRepository.deleteUserTrackedSymbol(
        userTrackedSymbols.mapNotNull { it.id }
    ).thenReturn(
        userTrackedSymbols
            .mapNotNull { it.userId }
            .map { userId ->
                buildKafkaNotificationStockPriceMessage(stockPriceConsumerRecord.value(), userId)
            }
    )

    private fun findUsersToNotify(it: ConsumerRecord<String, StockPrice>) =
        userTrackedSymbolRepository.findUserIdsToNotify(
            it.value().stockSymbolName,
            convertBigDecimalProtoToBigDecimal(it.value().price)
        )

    private fun buildKafkaNotificationStockPriceMessage(
        stockPrice: StockPrice,
        userId: ObjectId,
    ): SenderRecord<String, NotificationStockPrice, Nothing?> {
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                stockPrice.stockSymbolName,
                NotificationStockPrice.newBuilder().setStockSymbolName(stockPrice.stockSymbolName)
                    .setTimestamp(stockPrice.timestamp).setUserId(userId.toString()).setPrice(stockPrice.price).build()
            ),
            null
        )
    }
}
