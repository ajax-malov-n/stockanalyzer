package systems.ajax.malov.stockanalyzer.service.impl

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.test.test
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.notAggregatedResponseForFiveBestStockSymbolsWithStockRecords
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository

@ExtendWith(MockKExtension::class)
class MongoStockRecordAnalyzerServiceTest {

    @MockK
    private lateinit var stockRecordRepository: StockRecordRepository

    @InjectMockKs
    private lateinit var stockAnalyzerService: StockRecordAnalyzerServiceImpl

    @Test
    fun `getFiveBestStockSymbolsWithStockRecords calls repository and returns five bests stock symbols with records`() {
        // GIVEN
        val savedStock = savedStockRecord()
        val retrievedStocks = mapOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForFiveBestStockSymbolsWithStockRecords()
        every {
            stockRecordRepository.findTopNStockSymbolsWithStockRecords(
                eq(5),
                any(),
                any()
            )
        } returns Mono.just(retrievedStocks)

        // WHEN
        val actual = stockAnalyzerService.getFiveBestStockSymbolsWithStockRecords(5)

        // THEN
        actual.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            stockRecordRepository.findTopNStockSymbolsWithStockRecords(eq(5), any(), any())
        }
    }

    @Test
    fun `getAllManageableStocksSymbols calls repository and returns all stocks symbols`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        every { stockRecordRepository.findAllStockSymbols() } returns expected.toFlux()

        // WHEN
        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        // THEN
        verify { stockRecordRepository.findAllStockSymbols() }
        actual.test()
            .expectNext(expected)
            .verifyComplete()
    }
}
