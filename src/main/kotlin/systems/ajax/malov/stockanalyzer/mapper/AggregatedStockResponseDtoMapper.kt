package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockResponseDto
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockItemResponseDtoMapper.toAggregatedStockItemResponseDto

object AggregatedStockResponseDtoMapper {
    fun toAggregatedStockItemResponseDto(aggregatedData: List<Pair<String?, List<Stock>>>): AggregatedStockResponseDto =
        AggregatedStockResponseDto(
            aggregatedData.map { (symbol, stocks) ->
                symbol?.let { stocks.toAggregatedStockItemResponseDto(it) }
            }
        )
}
