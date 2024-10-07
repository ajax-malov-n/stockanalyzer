package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject.STOCK_QUEUE_GROUP
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS
import systems.ajax.malov.stockanalyzer.const.AppConst.DEFAULT_NUMBER_FOR_BEST_STOCKS
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController
import systems.ajax.malov.stockanalyzer.mapper.proto.GetFiveBestStockSymbolsWithStockRecordsRequestMapper.toGetFiveBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Component
class GetNBestStockSymbolsWithStockRecordsNatsController(
    override val connection: Connection,
    private val stockRecordAnalyzerService: StockRecordAnalyzerService,
) : NatsController<GetNBestStockSymbolsWithStockRecordsRequest, GetNBestStockSymbolsWithStockRecordsResponse> {

    override val queueGroup: String = STOCK_QUEUE_GROUP
    override val subject: String = GET_N_BEST_STOCK_SYMBOLS
    override val parser: Parser<GetNBestStockSymbolsWithStockRecordsRequest> =
        GetNBestStockSymbolsWithStockRecordsRequest.parser()

    override fun handle(request: GetNBestStockSymbolsWithStockRecordsRequest):
        Mono<GetNBestStockSymbolsWithStockRecordsResponse> {
        val requestedStocks = if (request.hasN()) {
            request.n
        } else {
            DEFAULT_NUMBER_FOR_BEST_STOCKS
        }
        return stockRecordAnalyzerService.getFiveBestStockSymbolsWithStockRecords(requestedStocks)
            .map { toGetFiveBestStockSymbolsWithStockRecordsRequest(it) }
    }
}
