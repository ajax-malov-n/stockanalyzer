package systems.ajax.malov.stockrecord.application.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockrecord.application.port.input.StockRecordAnalyzerServiceInPort
import systems.ajax.malov.stockrecord.application.port.out.StockRecordRepositoryOutPort
import systems.ajax.malov.stockrecord.domain.StockRecord
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class StockRecordAnalyzerService(
    @Qualifier("redisStockRecordRepository")
    private val redisStockRecordRepositoryOutPort: StockRecordRepositoryOutPort,
) : StockRecordAnalyzerServiceInPort {

    override fun getBestStockSymbolsWithStockRecords(
        quantity: Int,
    ): Mono<Map<String, List<StockRecord>>> {
        val dateOfRequest = Instant.now()
        val from = Date.from(dateOfRequest.minus(1, ChronoUnit.HOURS))
        val to = Date.from(dateOfRequest)
        return redisStockRecordRepositoryOutPort
            .findTopStockSymbolsWithStockRecords(quantity, from, to)
    }

    override fun getAllManageableStocksSymbols(): Flux<String> {
        return redisStockRecordRepositoryOutPort.findAllStockSymbols()
    }
}
