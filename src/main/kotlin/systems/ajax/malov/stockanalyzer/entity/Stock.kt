package systems.ajax.malov.stockanalyzer.entity

import java.math.BigDecimal
import java.time.Instant
import java.util.*

//@Document(collection = "stocks")
data class Stock(
//    @Id
    var id: UUID? = null,
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
