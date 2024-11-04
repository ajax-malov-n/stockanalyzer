package systems.ajax.malov.gateway.infrastructure.rest.dto

data class AggregatedStockRecordItemResponseDto(
    val stockSymbol: String,
    val data: List<ShortStockRecordResponseDto>,
)
