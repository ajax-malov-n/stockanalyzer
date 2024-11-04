package systems.ajax.malov.stockrecord.infrastructure.mongo.mapper

import org.bson.types.ObjectId
import systems.ajax.malov.stockrecord.domain.StockRecord
import systems.ajax.malov.stockrecord.infrastructure.mongo.entity.MongoStockRecord

object StockRecordMapper {

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
            id = ObjectId(id),
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
