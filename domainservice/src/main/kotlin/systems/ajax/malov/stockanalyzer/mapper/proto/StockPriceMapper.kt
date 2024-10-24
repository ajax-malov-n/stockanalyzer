package systems.ajax.malov.stockanalyzer.mapper.proto

import systems.ajax.malov.commonproto.stock.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.mapper.proto.TimestampProtoMapper.toTimestampProto

object StockPriceMapper {
    fun MongoStockRecord.toStockPrice(): StockPrice {
        return StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(currentPrice))
            .setStockSymbolName(symbol)
            .apply {
                dateOfRetrieval?.toTimestampProto()?.let { setTimestamp(it) }
            }
            .build()
    }
}
