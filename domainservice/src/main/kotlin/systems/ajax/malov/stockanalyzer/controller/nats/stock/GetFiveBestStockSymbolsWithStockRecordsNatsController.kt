package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_FIVE_BEST_STOCK_SYMBOLS
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController
import systems.ajax.malov.stockanalyzer.mapper.proto.GetFiveBestStockSymbolsWithStockRecordsRequestMapper.toGetFiveBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Component
class GetFiveBestStockSymbolsWithStockRecordsNatsController(
    override val connection: Connection,
    private val stockRecordAnalyzerServiceImpl: StockRecordAnalyzerService,
) : NatsController<GetFiveBestStockSymbolsWithStockRecordsRequest, GetFiveBestStockSymbolsWithStockRecordsResponse> {
    override val subject: String = GET_FIVE_BEST_STOCK_SYMBOLS
    override val parser: Parser<GetFiveBestStockSymbolsWithStockRecordsRequest> =
        GetFiveBestStockSymbolsWithStockRecordsRequest.parser()

    override fun handle(request: GetFiveBestStockSymbolsWithStockRecordsRequest):
        Mono<GetFiveBestStockSymbolsWithStockRecordsResponse> {
        return stockRecordAnalyzerServiceImpl.getFiveBestStockSymbolsWithStockRecords()
            .map { toGetFiveBestStockSymbolsWithStockRecordsRequest(it) }
    }
}
