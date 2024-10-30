package systems.ajax.malov.stockanalyzer.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice

interface NotificationStockPriceService {
    fun createUsersNotifications(stockPrice: StockPrice): Flux<Tuple2<NotificationStockPrice, ObjectId>>
    fun deleteUserTrackedSymbols(ids: List<ObjectId>): Mono<Unit>
}
