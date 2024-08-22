package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.LogExecutionTime
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import systems.ajax.malov.stockanalyzer.service.StockAnalyzerService

@Service
class StockAnalyzerServiceImpl(
    private val stockRepository: StockRepository,
) : StockAnalyzerService {

    @LogExecutionTime
    override fun getFiveBestStocksToBuy(): List<Pair<String?, List<Stock>>> {
        return stockRepository.findFiveBestStocksToBuy()
    }

    override fun getAllManageableStocksSymbols(): List<String?> {
        return stockRepository.getAllStockSymbols()
    }
}
