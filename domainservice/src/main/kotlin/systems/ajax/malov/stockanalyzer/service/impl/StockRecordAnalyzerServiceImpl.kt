package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.LogExecutionTime
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.repository.impl.RedisStockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class StockRecordAnalyzerServiceImpl(
    private val stockRedisRecordRepository: RedisStockRecordRepository,
    @Qualifier("mongoStockRecordRepository")
    private val stockMongoRecordRepository: StockRecordRepository,
) : StockRecordAnalyzerService {

    @LogExecutionTime
    override fun getBestStockSymbolsWithStockRecords(
        quantity: Int,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        val dateOfRequest = Instant.now()
        val from = Date.from(dateOfRequest.minus(1, ChronoUnit.HOURS))
        val to = Date.from(dateOfRequest)
        return stockRedisRecordRepository
            .findTopStockSymbolsWithStockRecords(
                quantity,
            ).switchIfEmpty(
                stockRedisRecordRepository.saveTopStockSymbolsWithStockRecords(
                    quantity,
                    stockMongoRecordRepository.findTopStockSymbolsWithStockRecords(
                        quantity,
                        from,
                        to
                    )
                )
            )
    }

    override fun getAllManageableStocksSymbols(): Flux<String> {
        return stockRedisRecordRepository.findAllStockSymbols()
            .switchIfEmpty(
                stockRedisRecordRepository.saveAllStockSymbols(stockMongoRecordRepository.findAllStockSymbols())
            )
    }
}
