package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.grpcapi.reqres.stock.AggregatedStockRecordItemResponse
import systems.ajax.malov.grpcapi.reqres.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse.ResponseCase
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse as InternalGetBestStockSymbolsWithStockRecordsResponse

object GetBestStockSymbolsWithStockRecordsResponseMapper {

    fun InternalGetBestStockSymbolsWithStockRecordsResponse.toGrpc(): GetBestStockSymbolsWithStockRecordsResponse {
        return when (responseCase!!) {
            ResponseCase.SUCCESS ->
                GetBestStockSymbolsWithStockRecordsResponse.newBuilder()
                    .apply {
                        successBuilder.addAllStockSymbols(
                            success.stockSymbolsList.map {
                                AggregatedStockRecordItemResponse.newBuilder().apply {
                                    stockSymbol = it.stockSymbol
                                    dataList.addAll(it.dataList)
                                }.build()
                            }
                        )
                    }.build()

            ResponseCase.FAILURE -> throw RuntimeException(failure.message)

            ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Required message is empty")
        }
    }
}
