package systems.ajax.malov.stockanalyzer.stockrecord.application.service

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
import stockanalyzer.stockrecord.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.stockrecord.utils.StockFixture.domainStockRecord
import stockanalyzer.stockrecord.utils.StockFixture.notAggregatedResponseForBestStockSymbolsWithStockRecords
import systems.ajax.malov.stockanalyzer.stockrecord.application.port.out.StockRecordRepositoryOutPort
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord

@ExtendWith(MockKExtension::class)
class StockRecordAnalyzerServiceTest {

    @MockK
    private lateinit var redisStockRecordRepositoryOutPort: StockRecordRepositoryOutPort

    @InjectMockKs
    private lateinit var stockAnalyzerService: StockRecordAnalyzerService

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val savedStock = domainStockRecord()
        val retrievedStocks = linkedMapOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForBestStockSymbolsWithStockRecords()
        val mono: Mono<Map<String, List<StockRecord>>> = retrievedStocks.toMono()
        every {
            redisStockRecordRepositoryOutPort.findTopStockSymbolsWithStockRecords(
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
            redisStockRecordRepositoryOutPort.findTopStockSymbolsWithStockRecords(eq(5), any(), any())
        }
    }

    @Test
    fun `getAllManageableStocksSymbols should retrieve all stock symbols`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        val flux = expected.toFlux()
        every { redisStockRecordRepositoryOutPort.findAllStockSymbols() } returns flux

        // WHEN
        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        // THEN
        actual.test()
            .expectNext(TEST_STOCK_SYMBOL)
            .verifyComplete()

        verify { redisStockRecordRepositoryOutPort.findAllStockSymbols() }
    }
}
