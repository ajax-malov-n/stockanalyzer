package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.mapper

import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.TimestampProtoMapper.toTimestampProto
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord

object StockRecordMapper {
    fun StockRecord.toStockPrice(): StockPrice {
        return StockPrice.newBuilder().apply {
            price = convertToBigDecimalProto(currentPrice)
            stockSymbolName = symbol
            dateOfRetrieval?.toTimestampProto()?.let { setTimestamp(it) }
        }.build()
    }
}
