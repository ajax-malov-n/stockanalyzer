package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.ShortStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.entity.StockRecord

object ShortStockRecordResponseDtoMapper {
    fun StockRecord.toShortStockRecordResponseDto() = ShortStockRecordResponseDto(
        openPrice,
        highPrice,
        lowPrice,
        currentPrice
    )
}
