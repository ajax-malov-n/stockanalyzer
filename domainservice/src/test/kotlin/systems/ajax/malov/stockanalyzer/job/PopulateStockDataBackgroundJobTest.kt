package systems.ajax.malov.stockanalyzer.job

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import stockanalyzer.utils.StockFixture.savedStockRecord
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockrecord.application.port.input.StockRecordClientApiInPort
import systems.ajax.malov.stockrecord.infrastructure.kafka.producer.StockPriceKafkaProducer

@ExtendWith(MockKExtension::class)
class PopulateStockDataBackgroundJobTest {

    @MockK
    private lateinit var stockRecordClientApiInPort: StockRecordClientApiInPort

    @MockK
    private lateinit var `stockRecordRepositoryOutPort.kt`: `StockRecordRepositoryOutPort.kt`

    @MockK
    private lateinit var stockPriceKafkaProducer: StockPriceKafkaProducer

    @InjectMockKs
    private lateinit var populateStockDataBackgroundJob: PopulateStockDataBackgroundJob

    @Test
    fun `aggregateStockData should retrieve data and then calls repository to save stocks`() {
        // GIVEN
        val retrievedStockRecords = listOf(unsavedStockRecord())
        every {
            stockRecordClientApiInPort.getAllStockRecords()
        } returns retrievedStockRecords.toFlux()
        every {
            `stockRecordRepositoryOutPort.kt`.insertAll(retrievedStockRecords)
        } returns listOf(savedStockRecord()).toFlux()
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
            `stockRecordRepositoryOutPort.kt`.insertAll(retrievedStockRecords)
        }
        every {
            stockPriceKafkaProducer.sendStockPrice(retrievedStockRecords)
        }
    }
}
