package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.gateway.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.gateway.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto
import systems.ajax.malov.internalapi.input.reqreply.stock.AggregatedStockRecordItemResponse

object AggregatedStockRecordItemResponseDtoMapper {
    fun AggregatedStockRecordItemResponse.toAggregatedStockRecordItemResponse(): AggregatedStockRecordItemResponseDto =
        AggregatedStockRecordItemResponseDto(
            stockSymbol = stockSymbol,
            data = dataList.map { stock -> stock.toShortStockRecordResponseDto() }
        )
}
