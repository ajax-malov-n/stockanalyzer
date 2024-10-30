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
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.gateway.mapper.GetAllManageableStockSymbolsResponseMapper.toGrpc
import systems.ajax.malov.gateway.mapper.GetBestStockSymbolsWithStockRecordsResponseMapper.toGrpc
import systems.ajax.malov.grpcapi.reqres.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.grpcapi.reqres.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.grpcapi.reqres.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.grpcapi.reqres.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.grpcapi.reqres.stock.GetStockPriceRequest
import systems.ajax.malov.internalapi.NatsSubject
import kotlin.test.assertEquals
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest as InternalGetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse as InternalGetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest as InternalGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse as InternalGetBestStockSymbolsWithStockRecordsResponse

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
        val requestNats = InternalGetBestStockSymbolsWithStockRecordsRequest.newBuilder().apply {
            quantity = 5
        }.build()
        val requestGrpc = GetBestStockSymbolsWithStockRecordsRequest.newBuilder().apply {
            quantity = 5
        }.build()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestNats,
                InternalGetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        } returns expected.toMono()

        // WHEN
        val response: Mono<GetBestStockSymbolsWithStockRecordsResponse> =
            stockGrpcService.getBestStockSymbolsWithStockRecords(requestGrpc.toMono())

        // THEN
        response.test()
            .expectNext(expected.toGrpc())
            .verifyComplete()
        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestNats,
                InternalGetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val expected = createGetBestStockSymbolsWithStockRecordsResponse()
        val requestNats = InternalGetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        val requestGrpc = GetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestNats,
                InternalGetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        } returns expected.toMono()

        // WHEN
        val response: Mono<GetBestStockSymbolsWithStockRecordsResponse> =
            stockGrpcService.getBestStockSymbolsWithStockRecords(requestGrpc.toMono())

        // THEN
        response.test()
            .expectNext(expected.toGrpc())
            .verifyComplete()
        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestNats,
                InternalGetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getAllManageableStockSymbols should retrieve all manageable stocks`() {
        // GIVEN
        val expected = InternalGetAllManageableStockSymbolsResponse.newBuilder()
            .apply {
                successBuilder.addAllSymbols(listOf(TEST_STOCK_SYMBOL))
            }
            .build()

        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                InternalGetAllManageableStockSymbolsRequest.getDefaultInstance(),
                InternalGetAllManageableStockSymbolsResponse.parser()
            )
        } returns expected.toMono()

        // WHEN
        val response: Mono<GetAllManageableStockSymbolsResponse> =
            stockGrpcService.getAllManageableStocksSymbols(GetAllManageableStockSymbolsRequest.getDefaultInstance())

        // THEN
        response.test()
            .expectNext(expected.toGrpc())
            .verifyComplete()

        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                InternalGetAllManageableStockSymbolsRequest.getDefaultInstance(),
                InternalGetAllManageableStockSymbolsResponse.parser()
            )
        }
    }

    @Test
    fun `getCurrentStockPrice should retrieve flux stock prices`() {
        // GIVEN
        val stockPrice = createStockPrice()
        val expected = listOf(stockPrice).toFlux()
        val request = GetStockPriceRequest.newBuilder().apply {
            symbolName = stockPrice.stockSymbolName
        }.build()

        every {
            natsClient.subscribeByStockSymbolName(
                stockPrice.stockSymbolName
            )
        } returns expected

        // WHEN
        val response: Flux<StockPrice> =
            stockGrpcService.getCurrentStockPrice(request)

        // THEN
        response.test()
            .assertNext { assertEquals(stockPrice, it) }
            .verifyComplete()

        verify {
            natsClient.subscribeByStockSymbolName(
                stockPrice.stockSymbolName
            )
        }
    }
}