package systems.ajax.malov.stockanalyzer.service

import reactor.core.publisher.Mono
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice

interface NotificationStockPriceService {
    fun createUsersNotifications(stockPrice: StockPrice): Mono<List<NotificationStockPrice>>
}
