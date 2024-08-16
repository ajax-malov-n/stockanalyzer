package systems.ajax.malov.stockanalyzer.service.impl

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import systems.ajax.malov.stockanalyzer.service.StockAggregationService
import systems.ajax.malov.stockanalyzer.service.StockClientApi

@Transactional(readOnly = true)
@Service
class StockAggregationServiceImpl(private val stockRepository: StockRepository,
                                  private val stockClientApi: StockClientApi) :
    StockAggregationService {

    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    override fun aggregateStockData() {
        stockClientApi.run {
            println("Performing aggregation task")
            getAllStocksData()
        }.also {
            stockRepository.insertAll(it)
            println("Aggregation task finished")
        }
    }
}