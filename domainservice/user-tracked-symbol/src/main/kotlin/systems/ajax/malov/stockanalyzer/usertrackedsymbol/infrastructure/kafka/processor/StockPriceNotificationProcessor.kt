package systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.kafka.processor

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.mapper.StockPriceMapper.toDomain
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.input.NotificationStockPriceServiceInPort
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.output.StockPriceNotificationProducerOutPort

@Component
class StockPriceNotificationProcessor(
    private val stockPriceNotificationProducer: StockPriceNotificationProducerOutPort,
    private val notificationStockPriceService: NotificationStockPriceServiceInPort,
) : KafkaHandler<StockPrice, TopicSingle> {

    override val groupId: String? = CONSUMER_STOCK_PRICE_GROUP
    override val parser: Parser<StockPrice> = StockPrice.parser()
    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE)

    override fun handle(kafkaEvent: KafkaEvent<StockPrice>): Mono<Unit> {
        return notificationStockPriceService
            .createUsersNotifications(kafkaEvent.data.toDomain())
            .collectList()
            .flatMapMany {
                stockPriceNotificationProducer.sendNotificationStockPrice(
                    it.map { (stockPrice, _) -> stockPrice }
                )
                    .thenMany(it.toMono())
            }.flatMap { notifications ->
                notificationStockPriceService
                    .deleteUserTrackedSymbols(notifications.map { (_, userTrackedStockId) -> userTrackedStockId })
            }
            .then(Unit.toMono())
    }

    companion object {
        private const val CONSUMER_STOCK_PRICE_GROUP = "stockPriceConsumerGroup"
    }
}
