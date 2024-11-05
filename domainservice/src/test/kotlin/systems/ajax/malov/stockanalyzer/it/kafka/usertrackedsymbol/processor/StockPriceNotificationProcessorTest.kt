package systems.ajax.malov.stockanalyzer.it.kafka.usertrackedsymbol.processor

import org.awaitility.Awaitility.await
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.scheduler.Schedulers
import stockanalyzer.utils.StockFixture.domainStockRecord
import stockanalyzer.utils.UserTrackedSymbolFixture.mongoUserTrackedSymbol
import systems.ajax.kafka.mock.KafkaMockExtension
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.util.IntegrationTestBase
import java.math.BigDecimal
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@ResourceLock(KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE)
@ResourceLock(KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE)
class StockPriceNotificationProcessorTest : IntegrationTestBase() {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var stockPrideProducer: StockPriceKafkaProducer

    @Test
    fun `should consume and process stock price message and publish notification message`() {
        // GIVEN
        val mongoStockRecords = listOf(domainStockRecord().copy(symbol = "ASDFG"))
        val mongoUserTrackedStock = mongoUserTrackedSymbol()
            .copy(
                id = ObjectId("6706a8343faaa9b224585999"),
                stockSymbolName = mongoStockRecords.first().symbol,
                thresholdPrice = mongoStockRecords.first().currentPrice!!.subtract(BigDecimal("0.5"))
            )
        mongoTemplate.insert(mongoUserTrackedStock)

        val receivedEvent = kafkaMockExtension.listen<NotificationStockPrice>(
            KafkaTopic.KafkaStockPriceEvents.NOTIFICATION_STOCK_PRICE,
            NotificationStockPrice.parser()
        )

        // WHEN
        stockPrideProducer.sendStockPrice(mongoStockRecords)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // THEN
        assertNotNull(
            receivedEvent.awaitFirst(
                { it.stockSymbolName == mongoStockRecords.first().symbol },
            ),
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

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
