package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.StockSymbol
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStock
import systems.ajax.malov.stockanalyzer.service.StockClientApi
import java.time.Instant

@Service
class FinnhubStockClientApi(private val finnhubStockApi: DefaultApi) : StockClientApi {

    @Value("\${api.finnhub.max_number_of_request_per_minute}")
    private var maxNumberOfRequestsPerMinute: Int = 0

    @Value("\${api.finnhub.exchange_name}")
    private lateinit var exchangeName: String

    override fun getAllStocksData(): List<Stock> {
        val retrievalDate: Instant = Instant.now()

        return getAllStocksInfo().asSequence()
            .take(maxNumberOfRequestsPerMinute)
            .mapNotNull { it.displaySymbol }.map {
                finnhubStockApi.quote(it).toStock(
                    it, retrievalDate
                )
            }.toList()
    }

    private fun getAllStocksInfo() = finnhubStockApi.stockSymbols(exchangeName)

    private fun DefaultApi.stockSymbols(exchange: String): List<StockSymbol> {
        return finnhubStockApi.stockSymbols(exchange, "", "", "")
    }
}
