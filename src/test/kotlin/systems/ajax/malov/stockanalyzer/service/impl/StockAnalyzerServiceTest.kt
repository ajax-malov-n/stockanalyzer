package systems.ajax.malov.stockanalyzer.service.impl


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import utils.StockFixture.TEST_STOCK_SYMBOL
import utils.StockFixture.notAggregatedResponseForFiveBestStocks
import utils.StockFixture.savedStock

@ExtendWith(MockitoExtension::class)
class StockAnalyzerServiceTest {

    @Mock
    private lateinit var stockRepository: StockRepository

    @InjectMocks
    private lateinit var stockAnalyzerService: StockAnalyzerServiceImpl

    @Test
    fun `getFiveBestStocksToBuy fun calls repository and returns five bests stocks`() {
        val savedStock = savedStock()
        val retrievedStocks = listOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForFiveBestStocks()
        whenever(stockRepository.findTopNStocks(5))
            .thenReturn(retrievedStocks)

        val actual = stockAnalyzerService.getFiveBestStocksToBuy()

        verify(stockRepository).findTopNStocks(5)
        assertEquals(expected, actual)
    }

    @Test
    fun `getAllManageableStocksSymbols fun calls repository and returns all stocks symbols`() {
        val expected = listOf(TEST_STOCK_SYMBOL)

        whenever(stockRepository.findAllStockSymbols())
            .thenReturn(expected)

        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        verify(stockRepository).findAllStockSymbols()
        assertEquals(expected, actual)
    }
}
