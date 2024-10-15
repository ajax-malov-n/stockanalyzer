package systems.ajax.malov.stockanalyzer.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import scala.Tuple2
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice

interface NotificationStockPriceService {
    fun createUsersNotifications(stockPrice: StockPrice): Mono<List<Tuple2<NotificationStockPrice, ObjectId>>>
    fun deleteUserTrackedSymbols(ids: List<ObjectId>): Mono<Unit>
}
