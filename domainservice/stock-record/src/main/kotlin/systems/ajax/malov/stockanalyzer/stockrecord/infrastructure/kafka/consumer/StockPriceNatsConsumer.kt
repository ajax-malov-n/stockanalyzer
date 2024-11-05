package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.consumer

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.stockanalyzer.stockrecord.application.port.out.StockMessageOutPort
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.mapper.StockPriceMapper.toDomain

@Component
class StockPriceNatsConsumer(
    private val publisher: StockMessageOutPort,
) : KafkaHandler<StockPrice, TopicSingle> {

    override val groupId: String? = NATS_CONSUMER_STOCK_PRICE_GROUP
    override val parser: Parser<StockPrice> = StockPrice.parser()
    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE)

    override fun handle(kafkaEvent: KafkaEvent<StockPrice>): Mono<Unit> {
        return publisher.publishStockPrice(
            kafkaEvent.data.toDomain()
        ).toMono()
    }

    companion object {
        private const val NATS_CONSUMER_STOCK_PRICE_GROUP = "natsStockPriceConsumerGroup"
    }
}
