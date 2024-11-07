package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.nats.mapper

import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.TimestampProtoMapper.toTimestampProto
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockPrice as DStockPrice

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
