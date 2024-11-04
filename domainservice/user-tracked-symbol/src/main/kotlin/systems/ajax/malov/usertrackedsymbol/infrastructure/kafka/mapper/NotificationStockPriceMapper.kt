package systems.ajax.malov.usertrackedsymbol.infrastructure.kafka.mapper

import systems.ajax.malov.internalapi.output.pubsub.stock.NotificationStockPrice
import systems.ajax.malov.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.shared.mapper.proto.TimestampProtoMapper.toTimestampProto
import systems.ajax.malov.usertrackedsymbol.domain.NotificationStockPrice as DNotificationStockPrice

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
