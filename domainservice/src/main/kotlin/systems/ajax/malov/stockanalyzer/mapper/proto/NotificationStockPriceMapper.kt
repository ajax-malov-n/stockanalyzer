package systems.ajax.malov.stockanalyzer.mapper.proto

import org.bson.types.ObjectId
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice

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
