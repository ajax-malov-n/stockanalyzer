package systems.ajax.malov.gateway.infrastructure.rest.dto

data class AggregatedStockRecordResponseDto(
    val stockSymbols: List<AggregatedStockRecordItemResponseDto>,
)
