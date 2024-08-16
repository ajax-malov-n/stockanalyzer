package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockItemResponse
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockResponse
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import systems.ajax.malov.stockanalyzer.service.StockAnalyzerService

@Service
class StockAnalyzerServiceImpl(
    private val stockRepository: StockRepository
) : StockAnalyzerService {

    override fun getFiveBestStocksToBuy(): List<AggregatedStockResponse> {
        return stockRepository.findFiveBestStocksToBuy()
            .map {
                AggregatedStockResponse(it.first?.let { symbol -> AggregatedStockItemResponse.fromStocks(symbol, it.second) })
            }
    }

    override fun getAllManageableStocksSymbols(): List<String?> {
        return stockRepository.getAllStockSymbols()
    }
}