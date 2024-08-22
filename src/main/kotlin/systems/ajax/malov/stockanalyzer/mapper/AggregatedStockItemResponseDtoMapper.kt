package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.mapper.ShortStockResponseDtoMapper.toShortStockResponseDto

object AggregatedStockItemResponseDtoMapper {
    fun List<Stock>.toAggregatedStockItemResponseDto(symbol: String): AggregatedStockItemResponseDto =
        AggregatedStockItemResponseDto(
            symbol,
            map { stock -> stock.toShortStockResponseDto() }
        )
}
