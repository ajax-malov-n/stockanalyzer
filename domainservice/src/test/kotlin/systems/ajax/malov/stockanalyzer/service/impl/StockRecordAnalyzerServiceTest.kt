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
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.notAggregatedResponseForBestStockSymbolsWithStockRecords
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.ReadOnlyStockRecordRepository

@ExtendWith(MockKExtension::class)
class StockRecordAnalyzerServiceTest {

    @MockK
    private lateinit var redisStockRecordRepository: ReadOnlyStockRecordRepository

    @InjectMockKs
    private lateinit var stockAnalyzerService: StockRecordAnalyzerServiceImpl

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val savedStock = savedStockRecord()
        val retrievedStocks = linkedMapOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForBestStockSymbolsWithStockRecords()
        val mono: Mono<Map<String, List<MongoStockRecord>>> = retrievedStocks.toMono()
        every {
            redisStockRecordRepository.findTopStockSymbolsWithStockRecords(
                eq(5),
                any(),
                any()
            )
        } returns mono

        // WHEN
        val actual = stockAnalyzerService.getBestStockSymbolsWithStockRecords(5)

        // THEN
        actual.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            redisStockRecordRepository.findTopStockSymbolsWithStockRecords(eq(5), any(), any())
        }
    }

    @Test
    fun `getAllManageableStocksSymbols should retrieve all stock symbols`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        val flux = expected.toFlux()
        every { redisStockRecordRepository.findAllStockSymbols() } returns flux

        // WHEN
        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        // THEN
        actual.test()
            .expectNext(TEST_STOCK_SYMBOL)
            .verifyComplete()

        verify { redisStockRecordRepository.findAllStockSymbols() }
    }
}
