package systems.ajax.malov.stockanalyzer.service.impl

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.test.test
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.notAggregatedResponseForBestStockSymbolsWithStockRecords
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.repository.impl.RedisStockRecordRepository

@ExtendWith(MockKExtension::class)
class StockRecordAnalyzerServiceTest {

    @MockK
    private lateinit var stockRecordRepository: StockRecordRepository

    @MockK
    private lateinit var redisStockRecordRepository: RedisStockRecordRepository

    @InjectMockKs
    private lateinit var stockAnalyzerService: StockRecordAnalyzerServiceImpl

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val savedStock = savedStockRecord()
        val retrievedStocks = linkedMapOf(Pair(TEST_STOCK_SYMBOL, listOf(savedStock)))
        val expected = notAggregatedResponseForBestStockSymbolsWithStockRecords()
        val mono: Mono<Map<String, List<MongoStockRecord>>> = Mono.just(retrievedStocks)
        every {
            stockRecordRepository.findTopStockSymbolsWithStockRecords(
                eq(5),
                any(),
                any()
            )
        } returns mono

        every {
            redisStockRecordRepository.findTopStockSymbolsWithStockRecords(
                eq(5),
            )
        } returns Mono.empty()

        every {
            redisStockRecordRepository.saveTopStockSymbolsWithStockRecords(
                eq(5),
                mono
            )
        } returns mono

        // WHEN
        val actual = stockAnalyzerService.getBestStockSymbolsWithStockRecords(5)

        // THEN
        actual.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            stockRecordRepository.findTopStockSymbolsWithStockRecords(eq(5), any(), any())
        }
        verify {
            redisStockRecordRepository.findTopStockSymbolsWithStockRecords(
                eq(5),
            )
        }
        verify {
            redisStockRecordRepository.saveTopStockSymbolsWithStockRecords(
                eq(5),
                eq(mono)
            )
        }
    }

    @Test
    fun `getAllManageableStocksSymbols should retrieve all stock symbols`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        val flux = expected.toFlux()
        every { stockRecordRepository.findAllStockSymbols() } returns flux
        every { redisStockRecordRepository.findAllStockSymbols() } returns Flux.empty()
        every { redisStockRecordRepository.saveAllStockSymbols(eq(flux)) } returns flux

        // WHEN
        val actual = stockAnalyzerService.getAllManageableStocksSymbols()

        // THEN
        actual.test()
            .expectNext(TEST_STOCK_SYMBOL)
            .verifyComplete()

        verify { stockRecordRepository.findAllStockSymbols() }
        verify { redisStockRecordRepository.findAllStockSymbols() }
        verify { redisStockRecordRepository.saveAllStockSymbols(eq(flux)) }
    }
}
