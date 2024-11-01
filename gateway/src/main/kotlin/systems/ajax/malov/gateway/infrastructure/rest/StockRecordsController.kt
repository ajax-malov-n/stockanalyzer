package systems.ajax.malov.gateway.infrastructure.rest

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.malov.gateway.application.port.output.StockMessageHandlerOutPort
import systems.ajax.malov.gateway.infrastructure.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.infrastructure.dto.GetBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.gateway.infrastructure.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.gateway.infrastructure.mapper.GetBestStockSymbolsWithStockRecordsRequestMapper.toInternal
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse.ResponseCase

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val messageHandlerOutPort: StockMessageHandlerOutPort) {

    @GetMapping("/best")
    fun getBestStockSymbolsWithStockRecords(
        @Valid requestDto: GetBestStockSymbolsWithStockRecordsRequestDto,
    ): Mono<AggregatedStockRecordResponseDto> =
        messageHandlerOutPort.getBestStockSymbolsWithStockRecords(
            requestDto.toInternal()
        ).map {
            it.toAggregatedStockItemResponseDto()
        }

    @GetMapping("/symbols")
    fun getAllManageableStockSymbols(): Mono<List<String>> =
        messageHandlerOutPort.getAllManageableStocksSymbols(GetAllManageableStockSymbolsRequest.getDefaultInstance())
            .handle { proto, sink ->
                when (proto.responseCase!!) {
                    ResponseCase.SUCCESS -> sink.next(proto.success.symbolsList)
                    ResponseCase.FAILURE -> sink.error(RuntimeException(proto.failure.message))
                    ResponseCase.RESPONSE_NOT_SET -> sink.error(RuntimeException("Required message is empty"))
                }
            }
}
