package systems.ajax.malov.usertrackedsymbol.application.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import systems.ajax.malov.stockrecord.domain.StockPrice
import systems.ajax.malov.usertrackedsymbol.application.port.input.NotificationStockPriceServiceInPort
import systems.ajax.malov.usertrackedsymbol.application.port.output.UserTrackedSymbolRepositoryOutPort
import systems.ajax.malov.usertrackedsymbol.domain.NotificationStockPrice
import systems.ajax.malov.usertrackedsymbol.domain.UserTrackedSymbol

@Service
class NotificationStockPriceService(
    private val userTrackedSymbolRepository: UserTrackedSymbolRepositoryOutPort,
) : NotificationStockPriceServiceInPort {
    override fun createUsersNotifications(
        stockPrice: StockPrice,
    ): Flux<Tuple2<NotificationStockPrice, String>> {
        return userTrackedSymbolRepository.findUserIdsToNotify(
            stockPrice.stockSymbolName,
            stockPrice.price
        )
            .map { userTrackedSymbols ->
                buildStockPriceNotifications(userTrackedSymbols, stockPrice)
            }
    }

    override fun deleteUserTrackedSymbols(ids: List<String>): Mono<Unit> {
        return userTrackedSymbolRepository
            .deleteUserTrackedSymbol(ids)
    }

    private fun buildStockPriceNotifications(
        userTrackedSymbol: UserTrackedSymbol,
        stockPrice: StockPrice,
    ): Tuple2<NotificationStockPrice, String> {
        return Tuples.of(
            NotificationStockPrice(
                userId = userTrackedSymbol.userId,
                stockPrice = stockPrice,
            ),
            userTrackedSymbol.id
        )
    }
}
