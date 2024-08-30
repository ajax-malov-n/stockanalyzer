package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.entity.StockRecord
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponseDto

object AggregatedStockRecordResponseDtoMapper {
    fun toAggregatedStockItemResponseDto(aggregatedData: Map<String, List<StockRecord>>):
            AggregatedStockRecordResponseDto =
        AggregatedStockRecordResponseDto(
            aggregatedData.map { (symbol, stocks) ->
                symbol.let { stocks.toAggregatedStockRecordItemResponseDto(it) }
            }
        )
}
