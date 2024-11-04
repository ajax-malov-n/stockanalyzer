package systems.ajax.malov.stockrecord.infrastructure.nats.mapper

import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.shared.mapper.proto.TimestampProtoMapper.toTimestampProto
import systems.ajax.malov.stockrecord.domain.StockPrice as DStockPrice

object StockPriceMapper {
    fun DStockPrice.toProto(): StockPrice {
        return StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(price))
            .setStockSymbolName(stockSymbolName)
            .apply {
                dateOfRetrieval.toTimestampProto()
            }
            .build()
    }
}
