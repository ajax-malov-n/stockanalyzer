package systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.output

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain.UserTrackedSymbol
import java.math.BigDecimal

interface UserTrackedSymbolRepositoryOutPort {
    fun findUserIdsToNotify(
        stockSymbolName: String,
        currentPrice: BigDecimal,
    ): Flux<UserTrackedSymbol>

    fun deleteUserTrackedSymbol(ids: List<String>): Mono<Unit>
}
