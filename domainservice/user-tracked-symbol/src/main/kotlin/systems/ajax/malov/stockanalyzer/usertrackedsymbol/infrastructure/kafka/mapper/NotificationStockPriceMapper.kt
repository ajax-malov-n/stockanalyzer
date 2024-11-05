package systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.kafka.mapper

import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.TimestampProtoMapper.toTimestampProto
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain.NotificationStockPrice as DNotificationStockPrice

object NotificationStockPriceMapper {
    fun DNotificationStockPrice.toNotificationStockPrice(): NotificationStockPrice {
        return NotificationStockPrice.newBuilder()
            .apply {
                userId = userId
                price = convertToBigDecimalProto(stockPrice.price)
                timestamp = stockPrice.dateOfRetrieval.toTimestampProto()
                userId = stockPrice.stockSymbolName
            }.build()
    }
}
