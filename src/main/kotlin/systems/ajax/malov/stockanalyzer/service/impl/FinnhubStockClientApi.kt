package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStock
import systems.ajax.malov.stockanalyzer.service.StockClientApi
import java.time.Instant

@Service
class FinnhubStockClientApi(private val finnhubStockApi: DefaultApi) : StockClientApi {

    @Value("\${api.finnhub.symbols}")
    private lateinit var symbols: List<String>

    override fun getAllStocksData(): List<Stock> {
        val retrievalDate = Instant.now()

        return symbols.mapNotNull {
            retrieveStock(it, retrievalDate)
        }.toList()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun retrieveStock(symbol: String, retrievalDate: Instant): Stock? {
        try {
            return finnhubStockApi.quote(symbol).toStock(symbol, retrievalDate)
        } catch (e: Exception) {
            log.error("Failed to retrieve data for symbol: $symbol at $retrievalDate. Error: ${e.message}")
            return null
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockAggregationServiceImpl::class.java)
    }
}
