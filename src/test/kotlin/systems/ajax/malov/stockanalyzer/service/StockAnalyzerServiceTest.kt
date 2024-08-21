package systems.ajax.malov.stockanalyzer.service

import StockFixture.TEST_STOCK_SYMBOL
import StockFixture.notAggregatedResponseForFiveBestStocks
import StockFixture.savedStock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import systems.ajax.malov.stockanalyzer.service.impl.StockAnalyzerServiceImpl

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
        whenever(stockRepository.findFiveBestStocksToBuy())
            .thenReturn(retrievedStocks)

        val actual = stockAnalyzerService.getFiveBestStocksToBuy()

        verify(stockRepository).findFiveBestStocksToBuy()
        assertEquals(expected, actual)
    }

    @Test
    fun `getAllManageableStocksSymbols fun calls repository and returns all stocks symbols`() {
        val expected = listOf(TEST_STOCK_SYMBOL)

        whenever(stockRepository.getAllStockSymbols())
            .thenReturn(expected)

        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        verify(stockRepository).getAllStockSymbols()
        assertEquals(expected, actual)
    }
}
