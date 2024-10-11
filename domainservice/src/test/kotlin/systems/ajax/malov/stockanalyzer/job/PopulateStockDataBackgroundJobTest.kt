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
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordClientApi

@ExtendWith(MockKExtension::class)
class PopulateStockDataBackgroundJobTest {

    @MockK
    private lateinit var stockRecordClientApi: StockRecordClientApi

    @MockK
    private lateinit var stockRecordRepository: StockRecordRepository

    @MockK
    private lateinit var stockPriceKafkaProducer: StockPriceKafkaProducer

    @InjectMockKs
    private lateinit var populateStockDataBackgroundJob: PopulateStockDataBackgroundJob

    @Test
    fun `aggregateStockData should retrieve data and then calls repository to save stocks`() {
        // GIVEN
        val retrievedStockRecords = listOf(unsavedStockRecord())
        every {
            stockRecordClientApi.getAllStockRecords()
        } returns retrievedStockRecords.toFlux()
        every {
            stockRecordRepository.insertAll(retrievedStockRecords)
        } returns listOf(savedStockRecord()).toFlux()
        every {
            stockPriceKafkaProducer.sendStockPrice(retrievedStockRecords)
        } returns Unit.toMono()

        // WHEN
        populateStockDataBackgroundJob.aggregateStockRecords()

        // THEN
        every {
            stockRecordClientApi.getAllStockRecords()
        }
        every {
            stockRecordRepository.insertAll(retrievedStockRecords)
        }
        every {
            stockPriceKafkaProducer.sendStockPrice(retrievedStockRecords)
        }
    }
}
