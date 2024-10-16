package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse

object AggregatedStockRecordResponseDtoMapper {

    fun GetBestStockSymbolsWithStockRecordsResponse.toAggregatedStockItemResponseDto():
        AggregatedStockRecordResponseDto = AggregatedStockRecordResponseDto(
        stockSymbols = success.stockSymbolsList.map { it.toAggregatedStockRecordItemResponse() }
    )
}
