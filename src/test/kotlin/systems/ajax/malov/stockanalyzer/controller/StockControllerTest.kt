package systems.ajax.malov.stockanalyzer.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import systems.ajax.malov.stockanalyzer.service.StockAggregationService
import systems.ajax.malov.stockanalyzer.service.StockAnalyzerService
import systems.ajax.malov.stockanalyzer.utils.TEST_STOCK_SYMBOL
import systems.ajax.malov.stockanalyzer.utils.aggregatedStockResponse
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class StockControllerTest {

    @Mock
    private lateinit var stockAnalyzerService: StockAnalyzerService

    @InjectMocks
    private lateinit var stockController: StockController

    @Test
    fun `getFiveBestStocks fun call service and retrieves five best stocks`(){
        whenever(stockAnalyzerService.getFiveBestStocksToBuy()).thenReturn(aggregatedStockResponse)

        val response = stockController.getFiveBestStocksToBuy()

        verify(stockAnalyzerService).getFiveBestStocksToBuy()
        assertEquals(response.statusCode,HttpStatus.OK)
    }

    @Test
    fun `getAllManageableStockSymbols fun call service and retrieves all manageable stocks`(){
        whenever(stockAnalyzerService.getAllManageableStocksSymbols()).thenReturn(listOf(TEST_STOCK_SYMBOL))

        val response = stockController.getAllManageableStockSymbols()

        verify(stockAnalyzerService).getAllManageableStocksSymbols()
        assertEquals(response.statusCode,HttpStatus.OK)
    }
}