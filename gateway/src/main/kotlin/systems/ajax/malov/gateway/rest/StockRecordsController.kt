package systems.ajax.malov.gateway.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val natsClient: NatsClient) {

    @GetMapping("/bestFive")
    fun getFiveBestStockSymbolsWithStockRecords(): Mono<AggregatedStockRecordResponseDto> =
        Mono.fromSupplier {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_FIVE_BEST_STOCK_SYMBOLS,
                GetFiveBestStockSymbolsWithStockRecordsRequest.getDefaultInstance(),
                GetFiveBestStockSymbolsWithStockRecordsResponse.parser()
            )
        }.map {
            it.toAggregatedStockItemResponseDto()
        }

    @GetMapping("/symbols")
    fun getAllManageableStockSymbols(): Mono<List<String>> =
        Mono.fromSupplier {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                GetAllManageableStockSymbolsRequest.getDefaultInstance(),
                GetAllManageableStockSymbolsResponse.parser()
            )
        }.map {
            it.symbolsList
        }
}
