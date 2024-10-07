package systems.ajax.malov.gateway.rest

import gateway.utils.StockFixture.TEST_STOCK_SYMBOL
import gateway.utils.StockFixture.createGetNBestStockSymbolsWithStockRecordsRequestDto
import gateway.utils.StockFixture.createGetNBestStockSymbolsWithStockRecordsResponse
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
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject

@ExtendWith(MockKExtension::class)
class StockRecordControllerTest {
    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var stockRecordsController: StockRecordsController

    @Test
    fun `getNBestStockSymbolsWithStockRecords should retrieve n best stock symbols with records`() {
        // GIVEN
        val natsResponse = createGetNBestStockSymbolsWithStockRecordsResponse()
        val requestDto = createGetNBestStockSymbolsWithStockRecordsRequestDto(5)
        val requestProto = GetNBestStockSymbolsWithStockRecordsRequest.newBuilder().setN(5).build()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestProto,
                GetNBestStockSymbolsWithStockRecordsResponse.parser()
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
                GetNBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }
    }

    @Test
    fun `getNBestStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
        // GIVEN
        val natsResponse = createGetNBestStockSymbolsWithStockRecordsResponse()
        val requestDto = createGetNBestStockSymbolsWithStockRecordsRequestDto(5)
            .copy(n = null)
        val requestProto = GetNBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        every {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
                requestProto,
                GetNBestStockSymbolsWithStockRecordsResponse.parser()
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
                GetNBestStockSymbolsWithStockRecordsResponse.parser()
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
