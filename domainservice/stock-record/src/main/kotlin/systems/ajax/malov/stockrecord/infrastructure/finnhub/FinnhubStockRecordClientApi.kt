package systems.ajax.malov.stockrecord.infrastructure.finnhub

import io.finnhub.api.apis.DefaultApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import systems.ajax.malov.stockrecord.application.port.input.StockRecordClientApiInPort
import systems.ajax.malov.stockrecord.domain.StockRecord
import systems.ajax.malov.stockrecord.infrastructure.finnhub.mapper.QuoteMapper.toStockRecord
import java.time.Instant

@Service
class FinnhubStockRecordClientApi(
    private val finnhubStockApi: DefaultApi,
    @Value("\${api.finnhub.symbols}") private val symbols: List<String>,
) : StockRecordClientApiInPort {

    override fun getAllStockRecords(): Flux<StockRecord> {
        val retrievalDate = Instant.now()

        return Flux.fromIterable(symbols)
            .flatMap { symbol ->
                retrieveStockRecord(symbol, retrievalDate)
            }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun retrieveStockRecord(symbol: String, retrievalDate: Instant): Mono<StockRecord> {
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
        private val log = LoggerFactory.getLogger(FinnhubStockRecordClientApi::class.java)
    }
}
