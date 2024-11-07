package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.finnhub

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import stockanalyzer.stockrecord.utils.StockFixture.domainStockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.application.port.input.StockRecordClientApiInPort
import systems.ajax.malov.stockanalyzer.stockrecord.application.port.out.StockRecordRepositoryOutPort
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.producer.StockPriceKafkaProducer

@ExtendWith(MockKExtension::class)
class PopulateStockDataBackgroundJobTest {

    @MockK
    private lateinit var stockRecordClientApiInPort: StockRecordClientApiInPort

    @MockK
    private lateinit var stockRecordRepositoryOutPort: StockRecordRepositoryOutPort

    @MockK
    private lateinit var stockPriceKafkaProducer: StockPriceKafkaProducer

    @InjectMockKs
    private lateinit var populateStockDataBackgroundJob: PopulateStockDataBackgroundJob

    @Test
    fun `aggregateStockData should retrieve data and then calls repository to save stocks`() {
        // GIVEN
        val retrievedStockRecords = listOf(domainStockRecord())
        every {
            stockRecordClientApiInPort.getAllStockRecords()
        } returns retrievedStockRecords.toFlux()
        every {
            stockRecordRepositoryOutPort.insertAll(retrievedStockRecords)
        } returns retrievedStockRecords.toFlux()
        every {
            stockPriceKafkaProducer.sendStockPrice(retrievedStockRecords)
        } returns Unit.toMono()

        // WHEN
        populateStockDataBackgroundJob.aggregateStockRecords()

        // THEN
        every {
            stockRecordClientApiInPort.getAllStockRecords()
        }
        every {
            stockRecordRepositoryOutPort.insertAll(retrievedStockRecords)
        }
        every {
            stockPriceKafkaProducer.sendStockPrice(retrievedStockRecords)
        }
    }
}
