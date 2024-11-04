package systems.ajax.malov.gateway.infrastructure.grpc

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.gateway.application.const.AppConst.QUANTITY_VALID_RANGE
import systems.ajax.malov.gateway.application.port.output.StockMessageOutPort
import systems.ajax.malov.gateway.infrastructure.mapper.GetAllManageableStockSymbolsRequestMapper.toInternal
import systems.ajax.malov.gateway.infrastructure.mapper.GetAllManageableStockSymbolsResponseMapper.toGrpc
import systems.ajax.malov.gateway.infrastructure.mapper.GetBestStockSymbolsWithStockRecordsRequestMapper.toInternal
import systems.ajax.malov.gateway.infrastructure.mapper.GetBestStockSymbolsWithStockRecordsResponseMapper.toGrpc
import systems.ajax.malov.grpcapi.reqres.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.grpcapi.reqres.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.grpcapi.reqres.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.grpcapi.reqres.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.grpcapi.reqres.stock.GetStockPriceRequest
import systems.ajax.malov.grpcapi.service.ReactorStockServiceGrpc
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest as InternalGetBestStockSymbolsWithStockRecordsRequest

@GrpcService
class StockGrpcService(
    private val messageHandlerOutPort: StockMessageOutPort,
) : ReactorStockServiceGrpc.StockServiceImplBase() {
    override fun getAllManageableStocksSymbols(
        request: Mono<GetAllManageableStockSymbolsRequest>,
    ): Mono<GetAllManageableStockSymbolsResponse> {
        return request
            .map { it.toInternal() }
            .flatMap {
                messageHandlerOutPort.getAllManageableStocksSymbols(it)
            }.map {
                it.toGrpc()
            }
    }

    override fun getBestStockSymbolsWithStockRecords(
        request: Mono<GetBestStockSymbolsWithStockRecordsRequest>,
    ): Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        return request
            .map { it.toInternal() }
            .flatMap { handleBestStockSymbolsWithStockRecords(it) }
    }

    override fun getCurrentStockPrice(request: Mono<GetStockPriceRequest>): Flux<StockPrice> {
        return request.flatMapMany {
            messageHandlerOutPort.getCurrentStockPrice(it.symbolName)
        }
    }

    fun handleBestStockSymbolsWithStockRecords(request: InternalGetBestStockSymbolsWithStockRecordsRequest):
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
        return messageHandlerOutPort.getBestStockSymbolsWithStockRecords(validRequest).map {
            it.toGrpc()
        }
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
