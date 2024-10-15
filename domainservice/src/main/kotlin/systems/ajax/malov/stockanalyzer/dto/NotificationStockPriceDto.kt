package systems.ajax.malov.stockanalyzer.dto

import org.bson.types.ObjectId
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice

data class NotificationStockPriceDto(
    val stockPrice: StockPrice,
    val userId: ObjectId,
)