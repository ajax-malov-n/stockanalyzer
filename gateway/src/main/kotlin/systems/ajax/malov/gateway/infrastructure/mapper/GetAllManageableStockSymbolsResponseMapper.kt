package systems.ajax.malov.gateway.infrastructure.mapper

import systems.ajax.malov.grpcapi.reqres.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse.ResponseCase
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse as InternalGetAllManageableStockSymbolsResponse

object GetAllManageableStockSymbolsResponseMapper {

    @SuppressWarnings("TooGenericExceptionThrown")
    fun InternalGetAllManageableStockSymbolsResponse.toGrpc(): GetAllManageableStockSymbolsResponse {
        return when (responseCase!!) {
            ResponseCase.SUCCESS -> GetAllManageableStockSymbolsResponse.newBuilder().also {
                it.successBuilder.addAllSymbols(success.symbolsList)
            }.build()

            ResponseCase.FAILURE -> throw RuntimeException(failure.message)

            ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Required message is empty")
        }
    }
}
