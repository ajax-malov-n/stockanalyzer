package systems.ajax.malov.gateway.infrastructure.dto

data class AggregatedStockRecordResponseDto(
    val stockSymbols: List<AggregatedStockRecordItemResponseDto>,
)
