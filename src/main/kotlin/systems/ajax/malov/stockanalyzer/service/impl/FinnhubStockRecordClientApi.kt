package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.entity.StockRecord
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStockRecord
import systems.ajax.malov.stockanalyzer.service.StockRecordClientApi

@Service
class FinnhubStockRecordClientApi(private val finnhubStockApi: DefaultApi) : StockRecordClientApi {

    @Value("\${api.finnhub.symbols}")
    private lateinit var symbols: List<String>

    override fun getAllStockRecords(): List<StockRecord> {
        val retrievalDate = Instant.now()

        return symbols.mapNotNull {
            retrieveStockRecord(it, retrievalDate)
        }.toList()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun retrieveStockRecord(symbol: String, retrievalDate: Instant): StockRecord? {
        try {
            return finnhubStockApi.quote(symbol).toStockRecord(symbol, retrievalDate)
        } catch (e: Exception) {
            log.error("Failed to retrieve data for symbol: $symbol at $retrievalDate. Error: ${e.message}")
            return null
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockRecordRecordAggregationServiceImpl::class.java)
    }
}
