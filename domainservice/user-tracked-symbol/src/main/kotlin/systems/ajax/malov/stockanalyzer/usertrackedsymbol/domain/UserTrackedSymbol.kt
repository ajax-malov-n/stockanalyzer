package systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain

import java.math.BigDecimal

data class UserTrackedSymbol(
    val id: String,
    val userId: String,
    val stockSymbolName: String,
    val thresholdPrice: BigDecimal,
)
