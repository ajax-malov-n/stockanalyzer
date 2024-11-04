package systems.ajax.malov.stockrecord.infrastructure.kafka.mapper

import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.shared.mapper.proto.TimestampProtoMapper.toTimestampProto
import systems.ajax.malov.stockrecord.domain.StockRecord

object StockRecordMapper {
    fun StockRecord.toStockPrice(): StockPrice {
        return StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(currentPrice))
            .setStockSymbolName(symbol)
            .apply {
                dateOfRetrieval?.toTimestampProto()?.let { setTimestamp(it) }
            }
            .build()
    }
}
