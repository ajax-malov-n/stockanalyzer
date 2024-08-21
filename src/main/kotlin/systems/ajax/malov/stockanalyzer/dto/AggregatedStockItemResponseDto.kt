package systems.ajax.malov.stockanalyzer.dto

data class AggregatedStockItemResponseDto(
    val stockSymbol: String,
    val data: List<ShortStockResponseDto>,
)
