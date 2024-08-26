package systems.ajax.malov.stockanalyzer.service.impl

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import systems.ajax.malov.stockanalyzer.service.StockAggregationService
import systems.ajax.malov.stockanalyzer.service.StockClientApi

@Service
class StockAggregationServiceImpl(
    private val stockRepository: StockRepository,
    private val stockClientApi: StockClientApi,
) :
    StockAggregationService {

    @Scheduled(cron = "0 0/1 * * * ?")
    override fun aggregateStocksData() {
        stockClientApi.run {
            log.info("Performing aggregation task")
            getAllStocksData()
        }.also {
            stockRepository.insertAll(it)
            log.info("Aggregation task finished")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockAggregationServiceImpl::class.java)
    }
}
