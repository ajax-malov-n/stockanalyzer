package systems.ajax.malov.stockanalyzer.kafka.processor

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import io.nats.client.Connection
import org.awaitility.Awaitility.await
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import stockanalyzer.utils.StockFixture.savedStockRecord
import stockanalyzer.utils.UserTrackedSymbolFixture.mongoUserTrackedSymbol
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.stockanalyzer.config.BaseKafkaConfiguration
import systems.ajax.malov.stockanalyzer.config.NatsDispatcherConfig
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.NatsControllerBeanPostProcessor
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import java.math.BigDecimal
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@SpringBootTest
@Import(StockPriceNotificationProcessorTest.MyKafkaTestConfiguration::class)
@MockBean(
    value = [
        Connection::class,
        NatsControllerBeanPostProcessor::class,
        NatsDispatcherConfig::class,
    ]
)
class StockPriceNotificationProcessorTest : AbstractMongoIntegrationTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var stockPrideProducer: StockPriceKafkaProducer

    @Autowired
    private lateinit var stockPriceNotificationProcessor: StockPriceNotificationProcessor

    @Autowired
    private lateinit var notificationStockPriceConsumer: KafkaReceiver<String, NotificationStockPrice>

    @Test
    fun `should publish messages`() {
        // GIVEN
        val mongoStockRecords = listOf(savedStockRecord().copy(symbol = "ASDFG"))
        val mongoUserTrackedStock = mongoUserTrackedSymbol()
            .copy(
                id = ObjectId("6706a8343faaa9b224585999"),
                stockSymbolName = mongoStockRecords.first().symbol,
                thresholdPrice = mongoStockRecords.first().currentPrice!!.subtract(BigDecimal("0.5"))
            )
        mongoTemplate.insert(mongoUserTrackedStock)
        stockPrideProducer.sendStockPrice(mongoStockRecords)
            .block()

        // WHEN
        stockPriceNotificationProcessor.subscribeToEvents()

        // THEN
        assertNotNull(
            notificationStockPriceConsumer.receive()
                .timeout(Duration.ofSeconds(5), Mono.empty())
                .filter { it.value().stockSymbolName == mongoStockRecords.first().symbol }
                .blockFirst(),
            "Published message must be consumed within 5 seconds"
        )

        await().pollDelay(Duration.ofMillis(100)).atMost(5, TimeUnit.SECONDS).untilAsserted {
            assertFalse(
                mongoTemplate.exists<MongoUserTrackedSymbol>(
                    Query.query(
                        Criteria.where(Fields.UNDERSCORE_ID)
                            .isEqualTo(mongoUserTrackedStock.id)
                    )
                ),
                "User tracked symbol must be not found after deletion"
            )
        }
    }

    class MyKafkaTestConfiguration(
        @Value("\${spring.kafka.bootstrap-servers}") val bootstrapServer: String,
        @Value("\${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String,
        kafkaProperties: KafkaProperties,
    ) : BaseKafkaConfiguration(bootstrapServer, schemaRegistryUrl, kafkaProperties) {

        @Bean
        fun kafkaNotificationStockPriceReceiver(): KafkaReceiver<String, NotificationStockPrice> {
            val customProperties: MutableMap<String, Any> = mutableMapOf(
                KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE
                    to
                    NotificationStockPrice::class.java.name
            )
            return createKafkaReceiver(
                baseConsumerProperties(customProperties),
                KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
                "stockPriceNotificationConsumerGroup",
            )
        }
    }
}
