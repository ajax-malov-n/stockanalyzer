package systems.ajax.malov.stockrecord.infrastructure.kafka.mapper

import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.shared.mapper.proto.BigDecimalProtoMapper.convertBigDecimalProtoToBigDecimal
import systems.ajax.malov.shared.mapper.proto.TimestampProtoMapper.toTimestamp
import systems.ajax.malov.stockrecord.domain.StockPrice as DStockPrice

object StockPriceMapper {
    fun StockPrice.toDomain(): DStockPrice {
        return DStockPrice(
            stockSymbolName = stockSymbolName,
            price = convertBigDecimalProtoToBigDecimal(price),
            dateOfRetrieval = timestamp.toTimestamp()
        )
    }
}
