package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse.ResponseCase

object AggregatedStockRecordResponseDtoMapper {

    @SuppressWarnings("FunctionReturnTypeSpacing")
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
