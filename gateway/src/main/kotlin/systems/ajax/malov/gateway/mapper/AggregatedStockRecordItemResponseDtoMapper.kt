package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.gateway.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.gateway.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponseDto as PAggregatedStockRecordItemResponse

object AggregatedStockRecordItemResponseDtoMapper {
    fun PAggregatedStockRecordItemResponse.toAggregatedStockRecordItemResponse(): AggregatedStockRecordItemResponseDto =
        AggregatedStockRecordItemResponseDto(
            stockSymbol = stockSymbol,
            data = dataList.map { stock -> stock.toShortStockRecordResponseDto() }
        )
}
