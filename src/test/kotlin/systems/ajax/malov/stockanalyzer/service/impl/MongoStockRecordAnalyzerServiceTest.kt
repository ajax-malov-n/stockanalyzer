package systems.ajax.malov.stockanalyzer.service.impl


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.notAggregatedResponseForFiveBestStockSymbolsWithStockRecords
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository

@ExtendWith(MockitoExtension::class)
class MongoStockRecordAnalyzerServiceTest {

    @Mock
    private lateinit var stockRecordRepository: StockRecordRepository

    @InjectMocks
    private lateinit var stockAnalyzerService: StockRecordAnalyzerServiceImpl

    @Test
    fun `getFiveBestStockSymbolsWithStockRecords calls repository and returns five bests stock symbols with records`() {
        val savedStock = savedStockRecord()
        val retrievedStocks = mapOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForFiveBestStockSymbolsWithStockRecords()
        whenever(stockRecordRepository.findTopNStockSymbolsWithStockRecords(5))
            .thenReturn(retrievedStocks)

        val actual = stockAnalyzerService.getFiveBestStockSymbolsWithStockRecords()

        verify(stockRecordRepository)
            .findTopNStockSymbolsWithStockRecords(5)
        assertEquals(expected, actual)
    }

    @Test
    fun `getAllManageableStocksSymbols calls repository and returns all stocks symbols`() {
        val expected = listOf(TEST_STOCK_SYMBOL)

        whenever(stockRecordRepository.findAllStockSymbols())
            .thenReturn(expected)

        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        verify(stockRecordRepository)
            .findAllStockSymbols()
        assertEquals(expected, actual)
    }
}
