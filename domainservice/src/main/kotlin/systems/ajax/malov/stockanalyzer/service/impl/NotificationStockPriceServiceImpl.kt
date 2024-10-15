package systems.ajax.malov.stockanalyzer.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import scala.Tuple2
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertBigDecimalProtoToBigDecimal
import systems.ajax.malov.stockanalyzer.mapper.proto.NotificationStockPriceMapper.toNotificationStockPrice
import systems.ajax.malov.stockanalyzer.repository.UserTrackedSymbolRepository
import systems.ajax.malov.stockanalyzer.service.NotificationStockPriceService

@Service
class NotificationStockPriceServiceImpl(
    private val userTrackedSymbolRepository: UserTrackedSymbolRepository,
) : NotificationStockPriceService {
    override fun createUsersNotifications(
        stockPrice: StockPrice,
    ): Mono<List<Tuple2<NotificationStockPrice, ObjectId>>> {
        return findUsersToNotify(stockPrice)
            .collectList()
            .flatMap { userTrackedSymbols ->
                buildStockPriceNotifications(userTrackedSymbols, stockPrice)
            }
    }

    override fun deleteUserTrackedSymbols(ids: List<ObjectId>): Mono<Unit> {
        return userTrackedSymbolRepository
            .deleteUserTrackedSymbol(
                ids
            )
    }

    @SuppressWarnings("UnsafeCallOnNullableType")
    private fun buildStockPriceNotifications(
        userTrackedSymbols: List<MongoUserTrackedSymbol>,
        stockPrice: StockPrice,
    ): Mono<List<Tuple2<NotificationStockPrice, ObjectId>>> {
        return userTrackedSymbols
            .map { userTrackedSymbol ->
                Tuple2(stockPrice.toNotificationStockPrice(userTrackedSymbol.userId!!), userTrackedSymbol.id!!)
            }.toMono()
    }

    private fun findUsersToNotify(stockPrice: StockPrice): Flux<MongoUserTrackedSymbol> {
        return userTrackedSymbolRepository.findUserIdsToNotify(
            stockPrice.stockSymbolName,
            convertBigDecimalProtoToBigDecimal(stockPrice.price)
        )
    }
}
