package systems.ajax.malov.stockanalyzer.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import systems.ajax.malov.stockanalyzer.service.StockAnalyzerService
import utils.StockFixture.TEST_STOCK_SYMBOL
import utils.StockFixture.aggregatedStockResponseDto
import utils.StockFixture.notAggregatedResponseForFiveBestStocks
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class StockControllerTest {

    @Mock
    private lateinit var stockAnalyzerService: StockAnalyzerService

    @InjectMocks
    private lateinit var stockController: StockController

    @Test
    fun `getFiveBestStocks fun call service and retrieves five best stocks`() {
        whenever(stockAnalyzerService.getFiveBestStocksToBuy()).thenReturn(notAggregatedResponseForFiveBestStocks())
        val expected = aggregatedStockResponseDto()

        val response = stockController.getFiveBestStocksToBuy()

        verify(stockAnalyzerService).getFiveBestStocksToBuy()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `getAllManageableStockSymbols fun call service and retrieves all manageable stocks`() {
        whenever(stockAnalyzerService.getAllManageableStocksSymbols()).thenReturn(listOf(TEST_STOCK_SYMBOL))

        val response = stockController.getAllManageableStockSymbols()

        verify(stockAnalyzerService).getAllManageableStocksSymbols()
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
