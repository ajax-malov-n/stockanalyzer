package systems.ajax.malov.gateway.rest

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.dto.GetBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse.ResponseCase
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val publisher: NatsMessagePublisher) {

    @GetMapping("/best")
    fun getBestStockSymbolsWithStockRecords(
        @Valid requestDto: GetBestStockSymbolsWithStockRecordsRequestDto,
    ): Mono<AggregatedStockRecordResponseDto> =
        publisher.request(
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
        publisher.request(
            NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
            GetAllManageableStockSymbolsRequest.getDefaultInstance(),
            GetAllManageableStockSymbolsResponse.parser()
        ).handle { proto, sink ->
            when (proto.responseCase!!) {
                ResponseCase.SUCCESS -> sink.next(proto.success.symbolsList)
                ResponseCase.FAILURE -> sink.error(RuntimeException(proto.failure.message))
                ResponseCase.RESPONSE_NOT_SET -> sink.error(RuntimeException("Required message is empty"))
            }
        }
}
