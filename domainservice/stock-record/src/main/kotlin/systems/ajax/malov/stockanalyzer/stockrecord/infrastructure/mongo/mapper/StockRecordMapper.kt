package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.mapper

import org.bson.types.ObjectId
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.entity.MongoStockRecord

object StockRecordMapper {

    @SuppressWarnings("UnsafeCallOnNullableType")
    fun MongoStockRecord.toDomain(): StockRecord {
        return StockRecord(
            id = id!!.toHexString(),
            symbol = symbol,
            openPrice = openPrice,
            highPrice = highPrice,
            lowPrice = lowPrice,
            currentPrice = currentPrice,
            previousClosePrice = previousClosePrice,
            change = change,
            percentChange = percentChange,
            dateOfRetrieval = dateOfRetrieval,
        )
    }

    fun StockRecord.toMongo(): MongoStockRecord {
        return MongoStockRecord(
            id = id?.let { ObjectId(it) },
            symbol = symbol,
            openPrice = openPrice,
            highPrice = highPrice,
            lowPrice = lowPrice,
            currentPrice = currentPrice,
            previousClosePrice = previousClosePrice,
            change = change,
            percentChange = percentChange,
            dateOfRetrieval = dateOfRetrieval,
        )
    }
}
