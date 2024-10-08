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
import stockanalyzer.utils.StockFixture.notAggregatedResponseForBestStockSymbolsWithStockRecords
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository

@ExtendWith(MockKExtension::class)
class MongoStockRecordAnalyzerServiceTest {

    @MockK
    private lateinit var stockRecordRepository: StockRecordRepository

    @InjectMockKs
    private lateinit var stockAnalyzerService: StockRecordAnalyzerServiceImpl

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val savedStock = savedStockRecord()
        val retrievedStocks = linkedMapOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForBestStockSymbolsWithStockRecords()
        every {
            stockRecordRepository.findTopStockSymbolsWithStockRecords(
                eq(5),
                any(),
                any()
            )
        } returns Mono.just(retrievedStocks)

        // WHEN
        val actual = stockAnalyzerService.getBestStockSymbolsWithStockRecords(5)

        // THEN
        actual.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            stockRecordRepository.findTopStockSymbolsWithStockRecords(eq(5), any(), any())
        }
    }

    @Test
    fun `getAllManageableStocksSymbols should retrieve all stock symbols`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        every { stockRecordRepository.findAllStockSymbols() } returns expected.toFlux()

        // WHEN
        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        // THEN
        verify { stockRecordRepository.findAllStockSymbols() }
        actual.test()
            .expectNext(TEST_STOCK_SYMBOL)
            .verifyComplete()
    }
}
