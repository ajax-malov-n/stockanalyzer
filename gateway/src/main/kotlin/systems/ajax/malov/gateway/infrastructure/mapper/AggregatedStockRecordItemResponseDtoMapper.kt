package systems.ajax.malov.gateway.infrastructure.mapper

import systems.ajax.malov.gateway.infrastructure.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto
import systems.ajax.malov.gateway.infrastructure.rest.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.internalapi.input.reqreply.stock.AggregatedStockRecordItemResponse

object AggregatedStockRecordItemResponseDtoMapper {
    fun AggregatedStockRecordItemResponse.toAggregatedStockRecordItemResponse(): AggregatedStockRecordItemResponseDto =
        AggregatedStockRecordItemResponseDto(
            stockSymbol = stockSymbol,
            data = dataList.map { stock -> stock.toShortStockRecordResponseDto() }
        )
}
