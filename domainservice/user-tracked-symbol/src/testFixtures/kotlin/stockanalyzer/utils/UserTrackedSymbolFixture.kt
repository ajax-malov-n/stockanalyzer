package stockanalyzer.utils

import org.bson.types.ObjectId
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol
import java.math.BigDecimal

object UserTrackedSymbolFixture {

    fun mongoUserTrackedSymbol() = MongoUserTrackedSymbol(
        id = ObjectId.get(),
        userId = ObjectId("6706a5693faaa9b224585583"),
        stockSymbolName = "IMAQ",
        thresholdPrice = BigDecimal("1.0")
    )
}
