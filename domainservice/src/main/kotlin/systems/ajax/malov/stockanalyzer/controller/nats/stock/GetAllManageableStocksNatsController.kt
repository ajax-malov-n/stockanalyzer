package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.malov.internalapi.NatsSubject.STOCK_QUEUE_GROUP
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Component
class GetAllManageableStocksNatsController(
    override val connection: Connection,
    private val stockRecordAnalyzerService: StockRecordAnalyzerService,
) : NatsController<GetAllManageableStockSymbolsRequest, GetAllManageableStockSymbolsResponse> {

    override val queueGroup: String = STOCK_QUEUE_GROUP
    override val subject: String = GET_ALL_MAN_SYMBOLS
    override val parser: Parser<GetAllManageableStockSymbolsRequest> = GetAllManageableStockSymbolsRequest.parser()

    override fun handle(request: GetAllManageableStockSymbolsRequest): Mono<GetAllManageableStockSymbolsResponse> {
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
}
