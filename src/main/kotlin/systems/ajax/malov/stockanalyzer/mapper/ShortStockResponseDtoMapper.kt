package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.ShortStockResponseDto
import systems.ajax.malov.stockanalyzer.entity.Stock

object ShortStockResponseDtoMapper {
    fun Stock.toShortStockResponseDto() = ShortStockResponseDto(
        openPrice,
        highPrice,
        lowPrice,
        currentPrice
    )
}
