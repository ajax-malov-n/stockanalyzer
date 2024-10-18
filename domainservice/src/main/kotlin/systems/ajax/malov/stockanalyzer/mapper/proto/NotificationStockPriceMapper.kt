package systems.ajax.malov.stockanalyzer.mapper.proto

import org.bson.types.ObjectId
import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice

object NotificationStockPriceMapper {

    fun StockPrice.toNotificationStockPrice(userId: ObjectId): NotificationStockPrice {
        return NotificationStockPrice.newBuilder()
            .setStockSymbolName(stockSymbolName)
            .setTimestamp(timestamp)
            .setUserId(userId.toString())
            .setPrice(price)
            .build()
    }
}
