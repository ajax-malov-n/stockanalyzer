package systems.ajax.malov.gateway.rest

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.dto.GetNBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val natsClient: NatsClient) {

    @GetMapping("/best")
    fun getFiveBestStockSymbolsWithStockRecords(
        @Valid requestDto: GetNBestStockSymbolsWithStockRecordsRequestDto,
    ): Mono<AggregatedStockRecordResponseDto> =
        natsClient.doRequest(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            GetNBestStockSymbolsWithStockRecordsRequest.newBuilder()
                .apply {
                    requestDto.n?.let { setN(it) }
                }
                .build(),
            GetNBestStockSymbolsWithStockRecordsResponse.parser()
        ).map {
            it.toAggregatedStockItemResponseDto()
        }

    @GetMapping("/symbols")
    fun getAllManageableStockSymbols(): Mono<List<String>> =
        natsClient.doRequest(
            NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
            GetAllManageableStockSymbolsRequest.getDefaultInstance(),
            GetAllManageableStockSymbolsResponse.parser()
        ).map {
            it.success.symbolsList
        }
}
