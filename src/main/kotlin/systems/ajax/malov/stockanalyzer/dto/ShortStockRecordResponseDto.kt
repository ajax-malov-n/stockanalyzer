package systems.ajax.malov.stockanalyzer.dto

import java.math.BigDecimal

data class ShortStockRecordResponseDto(
    val openPrice: BigDecimal?,
    val highPrice: BigDecimal?,
    val lowPrice: BigDecimal?,
    val currentPrice: BigDecimal?,
)
