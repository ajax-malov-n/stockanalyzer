package systems.ajax.malov.stockanalyzer.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice
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
    ): Flux<Tuple2<NotificationStockPrice, ObjectId>> {
        return userTrackedSymbolRepository.findUserIdsToNotify(
            stockPrice.stockSymbolName,
            convertBigDecimalProtoToBigDecimal(stockPrice.price)
        )
            .map { userTrackedSymbols ->
                buildStockPriceNotifications(userTrackedSymbols, stockPrice)
            }
    }

    override fun deleteUserTrackedSymbols(ids: List<ObjectId>): Mono<Unit> {
        return userTrackedSymbolRepository
            .deleteUserTrackedSymbol(ids)
    }

    @SuppressWarnings("UnsafeCallOnNullableType")
    private fun buildStockPriceNotifications(
        userTrackedSymbol: MongoUserTrackedSymbol,
        stockPrice: StockPrice,
    ): Tuple2<NotificationStockPrice, ObjectId> {
        return Tuples.of(stockPrice.toNotificationStockPrice(userTrackedSymbol.userId!!), userTrackedSymbol.id!!)
    }
}
