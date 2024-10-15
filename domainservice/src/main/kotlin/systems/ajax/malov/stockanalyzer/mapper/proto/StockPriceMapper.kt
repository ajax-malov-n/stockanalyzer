package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.Timestamp
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.mapper.proto.TimestampProtoMapper.toTimestampProto

object StockPriceMapper {
    fun MongoStockRecord.toStockPrice(): StockPrice {
        return StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(currentPrice))
            .setStockSymbolName(symbol)
            .setTimestamp(
                dateOfRetrieval?.toTimestampProto() ?: Timestamp.getDefaultInstance()
            )
            .build()
    }
}
