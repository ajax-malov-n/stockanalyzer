package systems.ajax.malov.gateway.grpc

import gateway.utils.StockFixture.TEST_STOCK_SYMBOL
import gateway.utils.StockFixture.createGetBestStockSymbolsWithStockRecordsResponse
import gateway.utils.StockFixture.createStockPrice
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
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetStockPriceRequest
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class StockGrpcServiceTest {
    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var stockGrpcService: StockGrpcService

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve best stock symbols with records`() {
        // GIVEN
        val expected = createGetBestStockSymbolsWithStockRecordsResponse()
        val request = GetBestStockSymbolsWithStockRecordsRequest.newBuilder().apply {
            quantity = 3
        }.build()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                request,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        } returns Mono.just(expected)

        // WHEN
        val response: Mono<GetBestStockSymbolsWithStockRecordsResponse> =
            stockGrpcService.getBestStockSymbolsWithStockRecords(request.toMono())

        // THEN
        response.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                request,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val expected = createGetBestStockSymbolsWithStockRecordsResponse()
        val request = GetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                request,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        } returns Mono.just(expected)

        // WHEN
        val response: Mono<GetBestStockSymbolsWithStockRecordsResponse> =
            stockGrpcService.getBestStockSymbolsWithStockRecords(request.toMono())

        // THEN
        response.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                request,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getAllManageableStockSymbols should retrieve all manageable stocks`() {
        // GIVEN
        val expected = GetAllManageableStockSymbolsResponse.newBuilder()
            .apply {
                successBuilder.addAllSymbols(listOf(TEST_STOCK_SYMBOL))
            }
            .build()

        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                GetAllManageableStockSymbolsRequest.getDefaultInstance(),
                GetAllManageableStockSymbolsResponse.parser()
            )
        } returns Mono.just(expected)

        // WHEN
        val response: Mono<GetAllManageableStockSymbolsResponse> =
            stockGrpcService.getAllManageableStocksSymbols(GetAllManageableStockSymbolsRequest.getDefaultInstance())

        // THEN
        response.test()
            .expectNext(expected)
            .verifyComplete()

        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                GetAllManageableStockSymbolsRequest.getDefaultInstance(),
                GetAllManageableStockSymbolsResponse.parser()
            )
        }
    }

    @Test
    fun `getCurrentStockPrice should retrieve flux stock prices`() {
        // GIVEN
        val stockPrice = createStockPrice()
        val expected = listOf(stockPrice).toFlux()

        every {
            natsClient.subscribe(
                NatsSubject.StockRequest.getStockPriceSubject(stockPrice.stockSymbolName)
            )
        } returns expected

        // WHEN
        val response: Flux<StockPrice> =
            stockGrpcService.getCurrentStockPrice(GetStockPriceRequest.newBuilder().apply {
                symbolName = stockPrice.stockSymbolName
            }.build())

        // THEN
        response.test()
            .assertNext { assertEquals(stockPrice, it) }
            .expectNextCount(0)
            .verifyComplete()

        verify {
            natsClient.subscribe(
                NatsSubject.StockRequest.getStockPriceSubject(stockPrice.stockSymbolName)
            )
        }
    }
}
