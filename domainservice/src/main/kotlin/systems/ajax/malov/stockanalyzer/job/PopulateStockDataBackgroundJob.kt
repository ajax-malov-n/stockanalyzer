package systems.ajax.malov.stockanalyzer.job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordClientApi

@Component
class PopulateStockDataBackgroundJob(
    @Qualifier("mongoStockRecordRepository")
    private val stockRecordRepository: StockRecordRepository,
    private val stockRecordClientApi: StockRecordClientApi,
    private val stockPriceKafkaProducer: StockPriceKafkaProducer,
) {

    @SchedulerLock(name = "aggregateStockRecords", lockAtLeastFor = "PT1M", lockAtMostFor = "PT3M")
    @Scheduled(cron = "0 0/1 * * * ?")
    fun aggregateStockRecords() {
        log.info("Performing aggregation task")

        stockRecordClientApi.getAllStockRecords()
            .collectList()
            .flatMap {
                stockPriceKafkaProducer.sendStockPrice(it)
                    .onErrorResume { error ->
                        log.error("Error was acquired during sending stock price messages", error)
                        Unit.toMono()
                    }
                    .thenReturn(it)
            }
            .flatMapMany { records ->
                stockRecordRepository.insertAll(records)
            }
            .then()
            .doOnSuccess {
                log.info("Aggregation task finished")
            }
            .block()
    }

    companion object {
        private val log = LoggerFactory.getLogger(PopulateStockDataBackgroundJob::class.java)
    }
}
