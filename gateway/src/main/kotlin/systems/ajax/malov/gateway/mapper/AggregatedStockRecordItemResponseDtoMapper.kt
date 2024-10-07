package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.gateway.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.gateway.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponse as PAggregatedStockRecordItemResponse

object AggregatedStockRecordItemResponseDtoMapper {
    fun PAggregatedStockRecordItemResponse.toAggregatedStockRecordItemResponse(): AggregatedStockRecordItemResponseDto =
        AggregatedStockRecordItemResponseDto(
            stockSymbol = stockSymbol,
            data = dataList.map { stock -> stock.toShortStockRecordResponseDto() }
        )
}
