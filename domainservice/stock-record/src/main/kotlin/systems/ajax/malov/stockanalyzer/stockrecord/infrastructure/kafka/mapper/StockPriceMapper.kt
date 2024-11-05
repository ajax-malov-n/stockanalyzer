package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.mapper

import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertBigDecimalProtoToBigDecimal
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.TimestampProtoMapper.toTimestamp
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockPrice as DStockPrice

object StockPriceMapper {
    fun StockPrice.toDomain(): DStockPrice {
        return DStockPrice(
            stockSymbolName = stockSymbolName,
            price = convertBigDecimalProtoToBigDecimal(price),
            dateOfRetrieval = timestamp.toTimestamp()
        )
    }
}
