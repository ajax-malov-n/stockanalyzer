package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockanalyzer.const.AppConst.DEFAULT_QUANTITY_FOR_BEST_STOCKS
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController
import systems.ajax.malov.stockanalyzer.mapper.proto.GetBestStockSymbolsWithStockRecordsRequestMapper.toGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Component
class GetBestStockSymbolsWithStockRecordsNatsController(
    override val connection: Connection,
    private val stockRecordAnalyzerService: StockRecordAnalyzerService,
) : NatsController<GetBestStockSymbolsWithStockRecordsRequest, GetBestStockSymbolsWithStockRecordsResponse> {

    override val queueGroup: String = STOCK_QUEUE_GROUP
    override val subject: String = GET_N_BEST_STOCK_SYMBOLS
    override val parser: Parser<GetBestStockSymbolsWithStockRecordsRequest> =
        GetBestStockSymbolsWithStockRecordsRequest.parser()

    override fun handle(request: GetBestStockSymbolsWithStockRecordsRequest):
        Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        val requestedStocks = if (request.hasQuantity()) {
            request.quantity
        } else {
            DEFAULT_QUANTITY_FOR_BEST_STOCKS
        }
        return stockRecordAnalyzerService.getBestStockSymbolsWithStockRecords(requestedStocks)
            .map { toGetBestStockSymbolsWithStockRecordsRequest(it) }
    }

    companion object {
        const val STOCK_QUEUE_GROUP = "stockQueueGroup"
    }
}
