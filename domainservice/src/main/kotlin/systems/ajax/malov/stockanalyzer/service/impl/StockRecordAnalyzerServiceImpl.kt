package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.LogExecutionTime
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class StockRecordAnalyzerServiceImpl(
    private val stockRecordRepository: StockRecordRepository,
) : StockRecordAnalyzerService {

    @LogExecutionTime
    override fun getBestStockSymbolsWithStockRecords(
        quantity: Int,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        val dateOfRequest = Instant.now()
        return stockRecordRepository
            .findTopStockSymbolsWithStockRecords(
                quantity,
                Date.from(dateOfRequest.minus(1, ChronoUnit.HOURS)),
                Date.from(dateOfRequest)
            )
    }

    override fun getAllManageableStocksSymbols(): Flux<String> {
        return stockRecordRepository.findAllStockSymbols()
    }
}
