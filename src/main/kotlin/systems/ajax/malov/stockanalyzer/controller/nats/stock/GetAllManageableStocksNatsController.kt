package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Component
class GetAllManageableStocksNatsController(
    override val connection: Connection,
    private val stockRecordAnalyzerServiceImpl: StockRecordAnalyzerService
)
    : NatsController<GetAllManageableStockSymbolsRequest,GetAllManageableStockSymbolsResponse> {

    override val subject: String = GET_ALL_MAN_SYMBOLS
    override val parser: Parser<GetAllManageableStockSymbolsRequest> = GetAllManageableStockSymbolsRequest.parser()

    override fun handle(request:GetAllManageableStockSymbolsRequest): Mono<GetAllManageableStockSymbolsResponse> {
        return stockRecordAnalyzerServiceImpl
            .getAllManageableStocksSymbols()
            .map {
                GetAllManageableStockSymbolsResponse.newBuilder()
                    .addAllSymbols(it)
                    .build()
            }
    }
}