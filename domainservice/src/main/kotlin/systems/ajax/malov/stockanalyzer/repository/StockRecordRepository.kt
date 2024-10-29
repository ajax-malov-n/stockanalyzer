package systems.ajax.malov.stockanalyzer.repository

import reactor.core.publisher.Flux
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordRepository : ReadOnlyStockRecordRepository {
    fun insertAll(mongoStockRecords: List<MongoStockRecord>): Flux<MongoStockRecord>
}
