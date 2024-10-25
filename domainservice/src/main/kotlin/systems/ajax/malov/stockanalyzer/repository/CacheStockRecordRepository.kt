package systems.ajax.malov.stockanalyzer.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

interface CacheStockRecordRepository {
    fun findTopStockSymbolsWithStockRecords(
        quantity: Int,
    ): Mono<Map<String, List<MongoStockRecord>>>

    fun findAllStockSymbols(): Flux<String>

    fun saveTopStockSymbolsWithStockRecords(
        quantity: Int,
        topStocksMap: Mono<Map<String, List<MongoStockRecord>>>,
    ): Mono<Map<String, List<MongoStockRecord>>>

    fun saveAllStockSymbols(stringFlux: Flux<String>): Flux<String>
}
