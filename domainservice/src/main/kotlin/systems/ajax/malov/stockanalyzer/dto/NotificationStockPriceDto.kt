package systems.ajax.malov.stockanalyzer.dto

import org.bson.types.ObjectId
import systems.ajax.malov.commonmodel.stock.StockPrice

data class NotificationStockPriceDto(
    val stockPrice: StockPrice,
    val userId: ObjectId,
)
