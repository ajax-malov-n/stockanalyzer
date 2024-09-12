package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.LogExecutionTime
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Service
class StockRecordAnalyzerServiceImpl(
    private val stockRecordRepository: StockRecordRepository,
) : StockRecordAnalyzerService {

    @Suppress("MagicNumber")
    @LogExecutionTime
    override fun getFiveBestStockSymbolsWithStockRecords(): Map<String, List<MongoStockRecord>> {
        return stockRecordRepository.findTopNStockSymbolsWithStockRecords(5)
    }

    override fun getAllManageableStocksSymbols(): List<String> {
        return stockRecordRepository.findAllStockSymbols()
    }
}
