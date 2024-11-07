package systems.ajax.malov.gateway.infrastructure.nats

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.gateway.application.port.output.StockMessageOutPort
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Service
class NatsStockMessageHandler(
    private val publisher: NatsMessagePublisher,
    private val manager: NatsHandlerManager,
) : StockMessageOutPort {
    override fun getAllManageableStocksSymbols(request: GetAllManageableStockSymbolsRequest):
        Mono<GetAllManageableStockSymbolsResponse> {
        return publisher.request(
            NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
            request,
            GetAllManageableStockSymbolsResponse.parser()
        )
    }

    override fun getBestStockSymbolsWithStockRecords(request: GetBestStockSymbolsWithStockRecordsRequest):
        Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        return publisher.request(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            request,
            GetBestStockSymbolsWithStockRecordsResponse.parser()
        )
    }

    override fun getCurrentStockPrice(stockSymbol: String): Flux<StockPrice> {
        return manager.subscribe(NatsSubject.StockRequest.getStockPriceSubject(stockSymbol)) { message ->
            StockPrice.parseFrom(message.data)
        }
    }
}
