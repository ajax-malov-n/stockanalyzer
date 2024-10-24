package systems.ajax.malov.gateway.grpc

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.gateway.client.NatsClient
import systems.ajax.malov.gateway.const.AppConst.QUANTITY_VALID_RANGE
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetStockPriceRequest
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice
import systems.ajax.malov.internalapi.service.ReactorStockServiceGrpc

@GrpcService
class StockGrpcService(
    private val natsClient: NatsClient,
) : ReactorStockServiceGrpc.StockServiceImplBase() {
    override fun getAllManageableStocksSymbols(
        request: Mono<GetAllManageableStockSymbolsRequest>,
    ): Mono<GetAllManageableStockSymbolsResponse> {
        return request.flatMap {
            natsClient.doRequest(
                NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
                it,
                GetAllManageableStockSymbolsResponse.parser()
            )
        }
    }

    override fun getBestStockSymbolsWithStockRecords(
        request: Mono<GetBestStockSymbolsWithStockRecordsRequest>,
    ): Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        return request.flatMap { handleBestStockSymbolsWithStockRecords(it) }
    }

    override fun getCurrentStockPrice(request: Mono<GetStockPriceRequest>): Flux<StockPrice> {
        return request.flatMapMany {
            natsClient.subscribe(
                it.symbolName,
            )
        }
    }

    fun handleBestStockSymbolsWithStockRecords(request: GetBestStockSymbolsWithStockRecordsRequest):
        Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        val validRequest = if (request.hasQuantity()) {
            if (request.quantity in QUANTITY_VALID_RANGE) {
                request
            } else {
                return createFailureResponse()
            }
        } else {
            request
        }
        return natsClient.doRequest(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            validRequest,
            GetBestStockSymbolsWithStockRecordsResponse.parser()
        )
    }

    private fun createFailureResponse(): Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        return GetBestStockSymbolsWithStockRecordsResponse
            .newBuilder()
            .apply {
                failureBuilder.message = "Quantity must be between 1 and 5"
            }
            .build()
            .toMono()
    }
}
