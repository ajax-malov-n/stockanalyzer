package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStockRecord
import systems.ajax.malov.stockanalyzer.service.StockRecordClientApi
import java.time.Instant

@Service
class FinnhubStockRecordClientApi(
    private val finnhubStockApi: DefaultApi,
    @Value("\${api.finnhub.symbols}") private val symbols: List<String>,
) : StockRecordClientApi {

    override fun getAllStockRecords(): Flux<MongoStockRecord> {
        val retrievalDate = Instant.now()

        return Flux.fromIterable(symbols)
            .flatMap { symbol ->
                retrieveStockRecord(symbol, retrievalDate)
            }
            .mapNotNull { it }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun retrieveStockRecord(symbol: String, retrievalDate: Instant): Mono<MongoStockRecord?> {
        return Mono.fromCallable {
            finnhubStockApi.quote(symbol).toStockRecord(symbol, retrievalDate)
        }
            .onErrorResume { e ->
                log.error("Failed to retrieve data for symbol: $symbol at $retrievalDate. Error: ${e.message}")
                Mono.empty()
            }
            .subscribeOn(Schedulers.boundedElastic())
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockRecordRecordAggregationServiceImpl::class.java)
    }
}
