package systems.ajax.malov.stockanalyzer.service

import systems.ajax.malov.stockanalyzer.entity.Stock

interface StockClientApi {
    fun getAllStocksData(): List<Stock>
}
