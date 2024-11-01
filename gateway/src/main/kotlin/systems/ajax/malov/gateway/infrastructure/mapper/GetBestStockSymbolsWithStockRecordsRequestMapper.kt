package systems.ajax.malov.gateway.infrastructure.mapper

import systems.ajax.malov.gateway.infrastructure.dto.GetBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.grpcapi.reqres.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest as InternalGetBestStockSymbolsWithStockRecordsRequest

object GetBestStockSymbolsWithStockRecordsRequestMapper {

    fun GetBestStockSymbolsWithStockRecordsRequest.toInternal(): InternalGetBestStockSymbolsWithStockRecordsRequest {
        return if (hasQuantity()) {
            InternalGetBestStockSymbolsWithStockRecordsRequest.newBuilder().also {
                it.setQuantity(quantity)
            }.build()
        } else {
            InternalGetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        }
    }

    fun GetBestStockSymbolsWithStockRecordsRequestDto.toInternal(): InternalGetBestStockSymbolsWithStockRecordsRequest {
        return InternalGetBestStockSymbolsWithStockRecordsRequest.newBuilder().also { requestBuilder ->
            quantity?.let { requestBuilder.setQuantity(it) }
        }.build()
    }
}
