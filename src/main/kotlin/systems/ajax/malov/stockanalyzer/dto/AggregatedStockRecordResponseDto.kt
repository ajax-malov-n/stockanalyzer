package systems.ajax.malov.stockanalyzer.dto

data class AggregatedStockRecordResponseDto(
    val stockSymbols: List<AggregatedStockRecordItemResponseDto>,
)
