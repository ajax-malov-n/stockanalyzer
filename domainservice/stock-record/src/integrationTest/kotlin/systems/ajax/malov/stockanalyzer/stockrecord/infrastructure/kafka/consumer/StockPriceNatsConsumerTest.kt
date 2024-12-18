package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.consumer

import org.awaitility.Awaitility.await
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.scheduler.Schedulers
import stockanalyzer.stockrecord.utils.StockFixture.domainStockRecord
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.stockanalyzer.stockrecord.application.port.out.StockPriceProducerOutPort
import systems.ajax.malov.stockanalyzer.stockrecord.util.IntegrationTestBase
import systems.ajax.nats.mock.junit5.NatsMockExtension
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertNotNull

@ResourceLock(KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE)
class StockPriceNatsConsumerTest : IntegrationTestBase() {
    @Autowired
    private lateinit var stockPrideProducer: StockPriceProducerOutPort

    @Test
    fun `should consumer stock price message`() {
        // GIVEN
        val testStock = domainStockRecord().copy(symbol = "NATSTEST")
        val mongoStockRecords = listOf(testStock)

        val captor = natsMockExt.subscribe(
            NatsSubject.StockRequest.getStockPriceSubject(testStock.symbol!!),
            StockPrice.parser()
        ).capture()

        // WHEN
        stockPrideProducer.sendStockPrice(mongoStockRecords)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // THEN
        await().pollDelay(Duration.ofMillis(100)).atMost(5, TimeUnit.SECONDS).untilAsserted {
            assertNotNull(
                captor.getCapturedMessages().any { it.stockSymbolName == mongoStockRecords.first().symbol },
                "Published message must be consumed within 5 seconds"
            )
        }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val natsMockExt: NatsMockExtension = NatsMockExtension()
    }
}
