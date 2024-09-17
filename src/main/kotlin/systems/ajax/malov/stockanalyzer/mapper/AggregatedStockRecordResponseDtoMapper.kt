package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponseDto

object AggregatedStockRecordResponseDtoMapper {
    fun toAggregatedStockItemResponseDto(
        aggregatedData: Map<String, List<MongoStockRecord>>,
    ): AggregatedStockRecordResponseDto =
        AggregatedStockRecordResponseDto(
            aggregatedData.map { (symbol, stocks) ->
                symbol.let { stocks.toAggregatedStockRecordItemResponseDto(it) }
            }
        )
}
