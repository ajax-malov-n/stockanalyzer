package systems.ajax.malov.stockanalyzer.dto

data class AggregatedStockRecordItemResponseDto(
    val stockSymbol: String,
    val data: List<ShortStockRecordResponseDto>,
)
