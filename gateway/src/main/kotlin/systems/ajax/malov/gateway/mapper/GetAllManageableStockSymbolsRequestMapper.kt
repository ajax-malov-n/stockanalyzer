package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.grpcapi.reqres.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest as InternalGetAllManageableStockSymbolsRequest

object GetAllManageableStockSymbolsRequestMapper {

    fun GetAllManageableStockSymbolsRequest.toInternal(): InternalGetAllManageableStockSymbolsRequest {
        return InternalGetAllManageableStockSymbolsRequest
            .getDefaultInstance()
    }
}
