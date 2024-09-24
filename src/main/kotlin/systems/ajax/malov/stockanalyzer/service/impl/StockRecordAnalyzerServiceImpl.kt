package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
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

    @Suppress("MagicNumber")
    @LogExecutionTime
    override fun getFiveBestStockSymbolsWithStockRecords(): Mono<Map<String, List<MongoStockRecord>>> {
        val dateOfRequest = Instant.now()
        return stockRecordRepository
            .findTopNStockSymbolsWithStockRecords(
                5,
                Date.from(dateOfRequest.minus(1, ChronoUnit.HOURS)),
                Date.from(dateOfRequest)
            )
    }

    override fun getAllManageableStocksSymbols(): Mono<List<String>> {
        return stockRecordRepository.findAllStockSymbols()
    }
}
