package systems.ajax.malov.stockrecord.infrastructure.nats.stock

import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockrecord.application.const.AppConst.DEFAULT_QUANTITY_FOR_BEST_STOCKS
import systems.ajax.malov.stockrecord.application.port.input.StockRecordAnalyzerServiceInPort
import systems.ajax.malov.stockrecord.infrastructure.nats.mapper.GetBestStockSymbolsWithStockRecordsRequestMapper.toGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class GetBestStockSymbolsWithStockRecordsNatsController(
    private val stockRecordAnalyzerServiceInPort: StockRecordAnalyzerServiceInPort,
) : ProtoNatsMessageHandler<GetBestStockSymbolsWithStockRecordsRequest, GetBestStockSymbolsWithStockRecordsResponse> {

    override val subject: String = GET_N_BEST_STOCK_SYMBOLS
    override val queue: String? = STOCK_QUEUE_GROUP
    override val parser: Parser<GetBestStockSymbolsWithStockRecordsRequest> =
        GetBestStockSymbolsWithStockRecordsRequest.parser()
    override val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun doOnUnexpectedError(
        inMsg: GetBestStockSymbolsWithStockRecordsRequest?,
        e: Exception,
    ): Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        return GetBestStockSymbolsWithStockRecordsResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }
            .build().toMono()
    }

    override fun doHandle(inMsg: GetBestStockSymbolsWithStockRecordsRequest):
        Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        val requestedStocks = if (inMsg.hasQuantity()) {
            inMsg.quantity
        } else {
            DEFAULT_QUANTITY_FOR_BEST_STOCKS
        }
        return stockRecordAnalyzerServiceInPort.getBestStockSymbolsWithStockRecords(requestedStocks)
            .map { toGetBestStockSymbolsWithStockRecordsRequest(it) }
    }

    companion object {
        const val STOCK_QUEUE_GROUP = "stockQueueGroup"
    }
}
