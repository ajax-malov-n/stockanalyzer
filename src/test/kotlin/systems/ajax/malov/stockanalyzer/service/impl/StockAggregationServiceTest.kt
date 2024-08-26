package systems.ajax.malov.stockanalyzer.service.impl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import systems.ajax.malov.stockanalyzer.service.StockClientApi
import utils.StockFixture.savedStock
import utils.StockFixture.unsavedStock

@ExtendWith(MockitoExtension::class)
class StockAggregationServiceTest {

    @Mock
    private lateinit var stockClientApi: StockClientApi

    @Mock
    private lateinit var stockRepository: StockRepository

    @InjectMocks
    private lateinit var stockAggregationServiceImpl: StockAggregationServiceImpl

    @Test
    fun `aggregateStockData fun calls external API to retrieve data and then calls repository to save stocks`() {
        val retrievedStocks = listOf(unsavedStock())
        whenever(stockClientApi.getAllStocksData()).thenReturn(retrievedStocks)
        whenever(stockRepository.insertAll(retrievedStocks)).thenReturn(listOf(savedStock()).toMutableList())

        stockAggregationServiceImpl.aggregateStockData()

        verify(stockClientApi).getAllStocksData()
        verify(stockRepository).insertAll(eq(retrievedStocks))
    }
}
