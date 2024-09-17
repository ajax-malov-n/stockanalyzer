package systems.ajax.malov.stockanalyzer.service.impl

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import systems.ajax.malov.stockanalyzer.service.StockRecordAggregationService
import systems.ajax.malov.stockanalyzer.service.StockRecordClientApi

@Service
class StockRecordRecordAggregationServiceImpl(
    private val stockRecordRepository: StockRecordRepository,
    private val stockRecordClientApi: StockRecordClientApi,
) :
    StockRecordAggregationService {

    @SchedulerLock(name = "aggregateStockRecords", lockAtLeastFor = "PT3M", lockAtMostFor = "PT5M")
    @Scheduled(cron = "0 0/1 * * * ?")
    override fun aggregateStockRecords() {
        stockRecordClientApi.run {
            log.info("Performing aggregation task")
            getAllStockRecords()
        }.also {
            stockRecordRepository.insertAll(it)
            log.info("Aggregation task finished")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockRecordRecordAggregationServiceImpl::class.java)
    }
}
