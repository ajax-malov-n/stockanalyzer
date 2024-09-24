package systems.ajax.malov.stockanalyzer.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.util.Date

interface StockRecordRepository {
    fun insertAll(mongoStockRecords: List<MongoStockRecord>): Flux<MongoStockRecord>
    fun findTopNStockSymbolsWithStockRecords(n: Int, from: Date, to: Date): Mono<Map<String, List<MongoStockRecord>>>
    fun findAllStockSymbols(): Mono<List<String>>
}
