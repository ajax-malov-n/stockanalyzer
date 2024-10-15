package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
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
    override fun createUsersNotifications(stockPrice: StockPrice): Mono<List<NotificationStockPrice>> {
        return findUsersToNotify(stockPrice)
            .collectList()
            .flatMap { userTrackedSymbols ->
                buildKafkaNotifications(userTrackedSymbols, stockPrice)
            }
    }

    private fun buildKafkaNotifications(
        userTrackedSymbols: List<MongoUserTrackedSymbol>,
        stockPrice: StockPrice,
    ): Mono<List<NotificationStockPrice>> {
        return userTrackedSymbolRepository
            .deleteUserTrackedSymbol(
                userTrackedSymbols.mapNotNull { it.id }
            ).thenReturn(
                userTrackedSymbols
                    .mapNotNull { it.userId }
                    .map { userId ->
                        stockPrice.toNotificationStockPrice(userId)
                    }
            )
    }

    private fun findUsersToNotify(stockPrice: StockPrice) =
        userTrackedSymbolRepository.findUserIdsToNotify(
            stockPrice.stockSymbolName,
            convertBigDecimalProtoToBigDecimal(stockPrice.price)
        )
}
