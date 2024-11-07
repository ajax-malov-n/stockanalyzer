package systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain

import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockPrice

data class NotificationStockPrice(
    val stockPrice: StockPrice,
    val userId: String,
)
