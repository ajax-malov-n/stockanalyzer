package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class GetAllManageableStocksNatsController(
    private val stockRecordAnalyzerService: StockRecordAnalyzerService,
) : ProtoNatsMessageHandler<GetAllManageableStockSymbolsRequest, GetAllManageableStockSymbolsResponse> {

    override val subject: String = GET_ALL_MAN_SYMBOLS
    override val queue: String? = STOCK_QUEUE_GROUP
    override val parser: Parser<GetAllManageableStockSymbolsRequest> = GetAllManageableStockSymbolsRequest.parser()
    override val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun doOnUnexpectedError(
        inMsg: GetAllManageableStockSymbolsRequest?,
        e: Exception,
    ): Mono<GetAllManageableStockSymbolsResponse> {
        return GetAllManageableStockSymbolsResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }
            .build().toMono()
    }

    override fun doHandle(inMsg: GetAllManageableStockSymbolsRequest): Mono<GetAllManageableStockSymbolsResponse> {
        return stockRecordAnalyzerService
            .getAllManageableStocksSymbols()
            .collectList()
            .map {
                GetAllManageableStockSymbolsResponse.newBuilder().apply {
                    successBuilder.addAllSymbols(it)
                }
                    .build()
            }
    }

    companion object {
        const val STOCK_QUEUE_GROUP = "stockQueueGroup"
    }
}
