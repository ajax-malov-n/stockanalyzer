package systems.ajax.malov.usertrackedsymbol.domain

import java.math.BigDecimal

data class UserTrackedSymbol(
    val id: String,
    val userId: String,
    val stockSymbolName: String,
    val thresholdPrice: BigDecimal,
)
