package systems.ajax.malov.stockanalyzer.service

import reactor.core.publisher.Flux
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface StockRecordClientApi {
    fun getAllStockRecords(): Flux<MongoStockRecord>
}
