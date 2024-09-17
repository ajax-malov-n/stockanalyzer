package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.ShortStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

object ShortStockRecordResponseDtoMapper {
    fun MongoStockRecord.toShortStockRecordResponseDto() = ShortStockRecordResponseDto(
        openPrice,
        highPrice,
        lowPrice,
        currentPrice
    )
}
