package systems.ajax.malov.stockrecord.domain

import java.math.BigDecimal
import java.time.Instant

data class StockRecord(
    var id: String?,
    val symbol: String?,
    val openPrice: BigDecimal?,
    val highPrice: BigDecimal?,
    val lowPrice: BigDecimal?,
    val currentPrice: BigDecimal?,
    val previousClosePrice: BigDecimal?,
    val change: BigDecimal?,
    val percentChange: BigDecimal?,
    val dateOfRetrieval: Instant?,
)
