package systems.ajax.malov.stockanalyzer.mapper

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto

object AggregatedStockRecordItemResponseDtoMapper {
    fun List<MongoStockRecord>.toAggregatedStockRecordItemResponseDto(symbol: String)
            : AggregatedStockRecordItemResponseDto =
        AggregatedStockRecordItemResponseDto(
            symbol,
            map { stock -> stock.toShortStockRecordResponseDto() }
        )
}
