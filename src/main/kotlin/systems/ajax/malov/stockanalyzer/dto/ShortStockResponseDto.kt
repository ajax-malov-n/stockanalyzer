package systems.ajax.malov.stockanalyzer.dto

import java.math.BigDecimal

data class ShortStockResponseDto(
    val openPrice: BigDecimal?,
    val highPrice: BigDecimal?,
    val lowPrice: BigDecimal?,
    val currentPrice: BigDecimal?,
)
