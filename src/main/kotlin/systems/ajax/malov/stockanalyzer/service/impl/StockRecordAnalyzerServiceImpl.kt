package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.entity.StockRecord
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@Service
class StockRecordAnalyzerServiceImpl(
    private val stockRecordRepository: StockRecordRepository,
) : StockRecordAnalyzerService {

    @Suppress("MagicNumber")
    override fun getFiveBestStockSymbolsWithStockRecords(): Map<String, List<StockRecord>> {
        return stockRecordRepository.findTopNStockSymbolsWithStockRecords(5)
    }

    override fun getAllManageableStocksSymbols(): Set<String> {
        return stockRecordRepository.findAllStockSymbols()
    }
}
