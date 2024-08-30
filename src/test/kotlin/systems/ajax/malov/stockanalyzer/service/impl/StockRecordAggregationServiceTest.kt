package systems.ajax.malov.stockanalyzer.service.impl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import stockanalyzer.utils.StockFixture.savedStockRecord
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordClientApi

@ExtendWith(MockitoExtension::class)
class StockRecordAggregationServiceTest {

    @Mock
    private lateinit var stockRecordClientApi: StockRecordClientApi

    @Mock
    private lateinit var stockRecordRepository: StockRecordRepository

    @InjectMocks
    private lateinit var stockRecordAggregationServiceImpl: StockRecordRecordAggregationServiceImpl

    @Test
    fun `aggregateStockData calls external API to retrieve data and then calls repository to save stocks`() {
        val retrievedStockRecords = listOf(unsavedStockRecord())
        whenever(stockRecordClientApi.getAllStockRecords())
            .thenReturn(retrievedStockRecords)
        whenever(stockRecordRepository.insertAll(retrievedStockRecords))
            .thenReturn(listOf(savedStockRecord()).toMutableList())

        stockRecordAggregationServiceImpl.aggregateStockRecords()

        verify(stockRecordClientApi)
            .getAllStockRecords()
        verify(stockRecordRepository)
            .insertAll(eq(retrievedStockRecords))
    }
}
