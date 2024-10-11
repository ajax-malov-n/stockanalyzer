package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.Timestamp
import com.squareup.wire.Instant
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto

object StockPriceMapper {
    fun MongoStockRecord.toStockPrice(): StockPrice {
        return StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(currentPrice))
            .setStockSymbolName(symbol)
            .setTimestamp(
                dateOfRetrieval?.let {
                    toTimestampProto(it)
                } ?: Timestamp.getDefaultInstance()
            )
            .build()
    }

    private fun toTimestampProto(dateOfRetrieval: Instant): Timestamp {
        return Timestamp.newBuilder()
            .setSeconds(dateOfRetrieval.epochSecond)
            .setNanos(dateOfRetrieval.nano)
            .build()
    }
}
