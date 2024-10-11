package systems.ajax.malov.gateway.rest

import gateway.utils.StockFixture.TEST_STOCK_SYMBOL
import gateway.utils.StockFixture.createGetBestStockSymbolsWithStockRecordsRequestDto
import gateway.utils.StockFixture.createGetBestStockSymbolsWithStockRecordsResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsResponse

@ExtendWith(MockKExtension::class)
class StockRecordControllerTest {
    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var stockRecordsController: StockRecordsController

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve best stock symbols with records`() {
        // GIVEN
        val natsResponse = createGetBestStockSymbolsWithStockRecordsResponse()
        val requestDto = createGetBestStockSymbolsWithStockRecordsRequestDto(5)
        val requestProto = GetBestStockSymbolsWithStockRecordsRequest.newBuilder().setQuantity(5).build()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestProto,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        } returns Mono.just(natsResponse)
        val expected = natsResponse.toAggregatedStockItemResponseDto()

        // WHEN
        val response: Mono<AggregatedStockRecordResponseDto> =
            stockRecordsController.getFiveBestStockSymbolsWithStockRecords(requestDto)

        // THEN
        response.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestProto,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val natsResponse = createGetBestStockSymbolsWithStockRecordsResponse()
        val requestDto = createGetBestStockSymbolsWithStockRecordsRequestDto(5)
            .copy(quantity = null)
        val requestProto = GetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestProto,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        } returns Mono.just(natsResponse)
        val expected = natsResponse.toAggregatedStockItemResponseDto()

        // WHEN
        val response: Mono<AggregatedStockRecordResponseDto> =
            stockRecordsController.getFiveBestStockSymbolsWithStockRecords(requestDto)

        // THEN
        response.test()
            .expectNext(expected)
            .verifyComplete()
        verify {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestProto,
                GetBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getAllManageableStockSymbols should retrieve all manageable stocks`() {
        // GIVEN
        val expected = listOf(TEST_STOCK_SYMBOL)
        val natsResponse = GetAllManageableStockSymbolsResponse.newBuilder()
            .apply {
                successBuilder.addAllSymbols(expected)
            }
            .build()

        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                GetAllManageableStockSymbolsRequest.getDefaultInstance(),
                GetAllManageableStockSymbolsResponse.parser()
            )
        } returns Mono.just(natsResponse)

        // WHEN
        val response: Mono<List<String>> = stockRecordsController.getAllManageableStockSymbols()

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
}
