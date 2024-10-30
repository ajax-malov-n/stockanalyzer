package systems.ajax.malov.stockanalyzer.kafka.consumer

import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.awaitility.Awaitility.await
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.stockanalyzer.kafka.processor.StockPriceNotificationProcessorTest
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.nats.handler.api.NatsHandlerManager

@SpringBootTest
@Import(StockPriceNotificationProcessorTest.MyKafkaTestConfiguration::class)
@ActiveProfiles("test")
class StockPriceNatsConsumerTest {

    @Autowired
    private lateinit var stockPriceNatsConsumer: StockPriceNatsConsumer

    @Autowired
    private lateinit var manger: NatsHandlerManager

    @Autowired
    private lateinit var stockPrideProducer: StockPriceKafkaProducer

    @Test
    fun `should consumer stock price message`() {
        // GIVEN
        val mongoStockRecords = listOf(unsavedStockRecord().copy(symbol = "NATSTEST"))
        stockPriceNatsConsumer.subscribeToEvents()

        val receivedNotifications = mutableListOf<StockPrice>()

        subscribe(mongoStockRecords[0].symbol!!)
            .doOnNext { receivedNotifications.add(it) }
            .timeout(Duration.ofSeconds(5), Mono.empty())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // WHEN
        stockPrideProducer.sendStockPrice(mongoStockRecords)
            .delaySubscription(Duration.ofSeconds(1))
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // THEN
        await().pollDelay(Duration.ofMillis(100)).atMost(5, TimeUnit.SECONDS).untilAsserted {
            assertNotNull(
                receivedNotifications.any { it.stockSymbolName == mongoStockRecords.first().symbol },
                "Published message must be consumed within 5 seconds"
            )
        }
    }

    fun subscribe(stockSymbolName: String): Flux<StockPrice> {
        return manger.subscribe(NatsSubject.StockRequest.getStockPriceSubject(stockSymbolName)) { message ->
            StockPrice.parseFrom(message.data)
        }
    }
}
