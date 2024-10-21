package systems.ajax.malov.stockanalyzer.controller.grpc.stock

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.service.ReactorStockServiceGrpc
import systems.ajax.malov.stockanalyzer.const.AppConst.DEFAULT_QUANTITY_FOR_BEST_STOCKS
import systems.ajax.malov.stockanalyzer.const.AppConst.QUANTITY_VALID_RANGE
import systems.ajax.malov.stockanalyzer.mapper.proto.GetBestStockSymbolsWithStockRecordsRequestMapper.toGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@GrpcService
class StockGrpcService(private val stockRecordAnalyzerService: StockRecordAnalyzerService) :
    ReactorStockServiceGrpc.StockServiceImplBase() {

    override fun getAllManageableStocksSymbols(
        request: Mono<GetAllManageableStockSymbolsRequest>,
    ): Mono<GetAllManageableStockSymbolsResponse> {
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

    override fun getBestStockSymbolsWithStockRecords(
        request: Mono<GetBestStockSymbolsWithStockRecordsRequest>,
    ): Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        return request.flatMap { handleBestStockSymbolsWithStockRecords(it) }
    }

    fun handleBestStockSymbolsWithStockRecords(request: GetBestStockSymbolsWithStockRecordsRequest):
        Mono<GetBestStockSymbolsWithStockRecordsResponse> {
        val requestedStocks = if (request.hasQuantity()) {
            if (request.quantity in QUANTITY_VALID_RANGE) {
                request.quantity
            } else {
                return createFailureResponse()
            }
        } else {
            DEFAULT_QUANTITY_FOR_BEST_STOCKS
        }
        return stockRecordAnalyzerService.getBestStockSymbolsWithStockRecords(requestedStocks)
            .map { toGetBestStockSymbolsWithStockRecordsRequest(it) }
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
