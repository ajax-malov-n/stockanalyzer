package systems.ajax.malov.usertrackedsymbol.infrastructure.mongo.mapper

import systems.ajax.malov.usertrackedsymbol.domain.UserTrackedSymbol
import systems.ajax.malov.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol
import java.math.BigDecimal

object UserTrackedSymbolMapper {

    fun MongoUserTrackedSymbol.toDomain(): UserTrackedSymbol {
        return UserTrackedSymbol(
            id = id.toHexString(),
            userId = userId.toHexString(),
            thresholdPrice = thresholdPrice ?: BigDecimal.ZERO,
            stockSymbolName = stockSymbolName.orEmpty()
        )
    }
}
