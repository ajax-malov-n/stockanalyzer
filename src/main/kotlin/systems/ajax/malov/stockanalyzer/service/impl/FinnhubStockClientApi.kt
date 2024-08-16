package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.StockSymbol
import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.constant.EXCHANGE_NAME
import systems.ajax.malov.stockanalyzer.constant.MAX_NUMBER_OF_REQUESTS_PER_MINUTE
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.service.StockClientApi
import java.time.Instant

@Service
class FinnhubStockClientApi(private val finnhubStockApi: DefaultApi) : StockClientApi {

    override fun getAllStocksData(): List<Stock> {
        val retrievalDate: Instant = Instant.now()

        return getAllStocksInfo().asSequence()
            .take(MAX_NUMBER_OF_REQUESTS_PER_MINUTE)
            .mapNotNull { it.displaySymbol }.map {
                Stock.fromQuote(
                    it, finnhubStockApi.quote(it), retrievalDate
                )
            }.toList()
    }

    private fun getAllStocksInfo() = finnhubStockApi.stockSymbols(EXCHANGE_NAME)

    private fun DefaultApi.stockSymbols(exchange:String) : List<StockSymbol> {
        return finnhubStockApi.stockSymbols(exchange, "", "", "")
    }
}