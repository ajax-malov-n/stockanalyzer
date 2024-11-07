package systems.ajax.malov.stockanalyzer.stockrecord.domain

import java.math.BigDecimal
import java.time.Instant

data class StockPrice(
    val stockSymbolName: String,
    val price: BigDecimal,
    val dateOfRetrieval: Instant,
)
