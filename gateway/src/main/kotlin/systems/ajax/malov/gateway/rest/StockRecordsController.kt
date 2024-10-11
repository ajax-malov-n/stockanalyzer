package systems.ajax.malov.gateway.rest

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.dto.GetBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsResponse

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val natsClient: NatsClient) {

    @GetMapping("/best")
    fun getFiveBestStockSymbolsWithStockRecords(
        @Valid requestDto: GetBestStockSymbolsWithStockRecordsRequestDto,
    ): Mono<AggregatedStockRecordResponseDto> =
        natsClient.doRequest(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            GetBestStockSymbolsWithStockRecordsRequest.newBuilder()
                .apply {
                    requestDto.quantity?.let { setQuantity(it) }
                }
                .build(),
            GetBestStockSymbolsWithStockRecordsResponse.parser()
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
