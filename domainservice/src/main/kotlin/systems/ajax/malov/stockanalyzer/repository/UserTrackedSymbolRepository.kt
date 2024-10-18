package systems.ajax.malov.stockanalyzer.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import java.math.BigDecimal

interface UserTrackedSymbolRepository {
    fun findUserIdsToNotify(
        stockSymbolName: String,
        currentPrice: BigDecimal,
    ): Flux<MongoUserTrackedSymbol>

    fun deleteUserTrackedSymbol(ids: List<ObjectId>): Mono<Unit>
}
