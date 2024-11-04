package systems.ajax.malov.usertrackedsymbol.domain

import systems.ajax.malov.stockrecord.domain.StockPrice

data class NotificationStockPrice(
    val stockPrice: StockPrice,
    val userId: String,
)
