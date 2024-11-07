package systems.ajax.malov.gateway.application.port.output

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse

interface StockMessageOutPort {
    fun getAllManageableStocksSymbols(
        request: GetAllManageableStockSymbolsRequest,
    ): Mono<GetAllManageableStockSymbolsResponse>

    fun getBestStockSymbolsWithStockRecords(
        request: GetBestStockSymbolsWithStockRecordsRequest,
    ): Mono<GetBestStockSymbolsWithStockRecordsResponse>

    fun getCurrentStockPrice(stockSymbol: String): Flux<StockPrice>
}
