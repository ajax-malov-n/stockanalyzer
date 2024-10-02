package systems.ajax.malov.gateway.rest

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.aggregatedStockRecordResponseDto
import stockanalyzer.utils.StockFixture.notAggregatedResponseForFiveBestStockSymbolsWithStockRecords
import systems.ajax.malov.stockanalyzer.controller.rest.StockRecordsController
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@ExtendWith(MockKExtension::class)
class StockRecordControllerTest {

    @MockK
    private lateinit var stockRecordAnalyzerService: StockRecordAnalyzerService

    @InjectMockKs
    private lateinit var stockRecordsController: StockRecordsController

    @Test
    fun `getFiveBestStockSymbolsWithStockRecords calls service and retrieves five best stocks symbols with records`() {
        // GIVEN
        every {
            stockRecordAnalyzerService.getFiveBestStockSymbolsWithStockRecords()
        } returns Mono.just(notAggregatedResponseForFiveBestStockSymbolsWithStockRecords())
        val expected = aggregatedStockRecordResponseDto()

        // WHEN
        val response: Mono<AggregatedStockRecordResponseDto> =
            stockRecordsController.getFiveBestStockSymbolsWithStockRecords()

        // THEN
        response.test()
            .expectNext(expected)
            .verifyComplete()
        verify { stockRecordAnalyzerService.getFiveBestStockSymbolsWithStockRecords() }
    }

    @Test
    fun `getAllManageableStockSymbols calls service and retrieves all manageable stocks`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        every {
            stockRecordAnalyzerService.getAllManageableStocksSymbols()
        } returns Mono.just(expected)

        // WHEN
        val response: Mono<List<String>> = stockRecordsController.getAllManageableStockSymbols()

        // THEN
        verify { stockRecordAnalyzerService.getAllManageableStocksSymbols() }
        response.test()
            .expectNext(expected)
            .verifyComplete()
    }
}
