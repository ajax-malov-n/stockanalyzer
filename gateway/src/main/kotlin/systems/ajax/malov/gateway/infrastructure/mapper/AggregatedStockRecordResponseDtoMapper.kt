package systems.ajax.malov.gateway.infrastructure.mapper

import systems.ajax.malov.gateway.infrastructure.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponse
import systems.ajax.malov.gateway.infrastructure.rest.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse.ResponseCase

object AggregatedStockRecordResponseDtoMapper {

    @SuppressWarnings("FunctionReturnTypeSpacing", "TooGenericExceptionThrown")
    fun GetBestStockSymbolsWithStockRecordsResponse.toAggregatedStockItemResponseDto():
        AggregatedStockRecordResponseDto {
        return when (responseCase!!) {
            ResponseCase.SUCCESS -> AggregatedStockRecordResponseDto(
                stockSymbols = success.stockSymbolsList.map { it.toAggregatedStockRecordItemResponse() }
            )

            ResponseCase.FAILURE -> throw RuntimeException(failure.message)
            ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Required message is empty")
        }
    }
}
