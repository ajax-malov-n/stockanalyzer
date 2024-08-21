package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.entity.Stock

interface StockAnalyzerService {
    fun getFiveBestStocksToBuy(): List<Pair<String?, List<Stock>>>
    fun getAllManageableStocksSymbols(): List<String?>
}
