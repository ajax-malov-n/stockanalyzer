package systems.ajax.malov.gateway.infrastructure.rest.dto

import java.math.BigDecimal

data class ShortStockRecordResponseDto(
    val openPrice: BigDecimal?,
    val highPrice: BigDecimal?,
    val lowPrice: BigDecimal?,
    val currentPrice: BigDecimal?,
)
