package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.dto.AggregatedStockResponse

interface StockAnalyzerService {
    fun getFiveBestStocksToBuy() : AggregatedStockResponse
    fun getAllManageableStocksSymbols(): List<String?>
}