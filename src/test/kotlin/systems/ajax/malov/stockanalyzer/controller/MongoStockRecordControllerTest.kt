package systems.ajax.malov.stockanalyzer.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.aggregatedStockRecordResponseDto
import stockanalyzer.utils.StockFixture.notAggregatedResponseForFiveBestStockSymbolsWithStockRecords
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class MongoStockRecordControllerTest {

    @Mock
    private lateinit var stockRecordAnalyzerService: StockRecordAnalyzerService

    @InjectMocks
    private lateinit var stockRecordsController: StockRecordsController

    @Test
    fun `getFiveBestStockSymbolsWithStockRecords calls service and retrieves five best stocks symbols with records`() {
        whenever(stockRecordAnalyzerService.getFiveBestStockSymbolsWithStockRecords())
            .thenReturn(notAggregatedResponseForFiveBestStockSymbolsWithStockRecords())

        val expected = aggregatedStockRecordResponseDto()

        val response = stockRecordsController.getFiveBestStockSymbolsWithStockRecords()

        verify(stockRecordAnalyzerService)
            .getFiveBestStockSymbolsWithStockRecords()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `getAllManageableStockSymbols calls service and retrieves all manageable stocks`() {
        whenever(stockRecordAnalyzerService.getAllManageableStocksSymbols())
            .thenReturn(listOf(TEST_STOCK_SYMBOL))

        val response = stockRecordsController.getAllManageableStockSymbols()

        verify(stockRecordAnalyzerService)
            .getAllManageableStocksSymbols()
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
