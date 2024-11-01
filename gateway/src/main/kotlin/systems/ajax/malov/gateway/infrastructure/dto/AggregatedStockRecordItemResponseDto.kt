package systems.ajax.malov.gateway.infrastructure.dto

data class AggregatedStockRecordItemResponseDto(
    val stockSymbol: String,
    val data: List<ShortStockRecordResponseDto>,
)
