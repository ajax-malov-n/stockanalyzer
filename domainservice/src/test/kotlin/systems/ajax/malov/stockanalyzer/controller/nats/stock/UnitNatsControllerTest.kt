package systems.ajax.malov.stockanalyzer.controller.nats.stock

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockrecord.application.port.input.StockRecordAnalyzerServiceInPort
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UnitNatsControllerTest {
    @InjectMockKs
    private lateinit var getBestNatsController: GetBestStockSymbolsWithStockRecordsNatsController

    @MockK
    @SuppressWarnings("UnusedPrivateProperty")
    private lateinit var stockService: StockRecordAnalyzerServiceInPort

    @Test
    fun `doOnUnexpectedError should return response with error message`() {
        // GIVEN
        val mockRequest = mockk<GetBestStockSymbolsWithStockRecordsRequest>()
        val exceptionMessage = "An unexpected error occurred"
        val mockException = Exception(exceptionMessage)

        // WHEN
        val result: Mono<GetBestStockSymbolsWithStockRecordsResponse> =
            getBestNatsController.doOnUnexpectedError(mockRequest, mockException)

        // THEN
        result.test()
            .expectNextMatches { response ->
                response.failure.message == exceptionMessage
            }
            .verifyComplete()
    }

    @Test
    fun `doOnUnexpectedError should return response with empty error message`() {
        // GIVEN
        val mockRequest = mockk<GetBestStockSymbolsWithStockRecordsRequest>()
        val mockException = Exception()

        // WHEN
        val result: Mono<GetBestStockSymbolsWithStockRecordsResponse> =
            getBestNatsController.doOnUnexpectedError(mockRequest, mockException)

        // THEN
        result.test()
            .expectNextMatches { response ->
                response.failure.message == ""
            }
            .verifyComplete()
    }
}
