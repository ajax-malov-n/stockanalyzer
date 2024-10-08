package systems.ajax.malov.gateway.dto

data class AggregatedStockRecordItemResponseDto(
    val stockSymbol: String,
    val data: List<ShortStockRecordResponseDto>,
)
