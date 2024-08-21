package systems.ajax.malov.stockanalyzer.repository.impl

import org.springframework.stereotype.Repository
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.*


@Repository
class StockInMemoryRepositoryImpl : StockRepository {
    private val db: HashMap<UUID, Stock> = HashMap()

    override fun insertAll(stocks: List<Stock>): MutableCollection<Stock> {
        stocks.forEach {
            val id = UUID.randomUUID()
            it.id = id
            db[id] = it
        }
        return stocks.toMutableList()
    }

    override fun findFiveBestStocksToBuy(): List<Pair<String?, List<Stock>>> {
        if (db.isEmpty()) return emptyList()

        val maxPercentChange = db.asSequence().mapNotNull { it.value.percentChange }.maxBy { it }
        val maxChange = db.asSequence().mapNotNull { it.value.change }.maxBy { it }

        return db.asSequence().filterNot { it.value.symbol == null }.filter { stock ->
                stock.value.dateOfRetrieval?.isBefore(Instant.now().plus(Duration.ofHours(1))) == true
            }.map { it.value }.groupBy { it.symbol }.toList().sortedByDescending { (_, stockList) ->
                val avgChange = stockList.mapNotNull { it.change }
                    .reduce { acc, bigDecimal -> acc + bigDecimal } / BigDecimal.valueOf(stockList.size.toLong())
                val avgPercentChange = stockList.mapNotNull { it.percentChange }
                    .reduce { acc, bigDecimal -> acc + bigDecimal } / BigDecimal.valueOf(stockList.size.toLong())

                ((avgChange / maxChange) * BigDecimal.valueOf(0.5)) + ((avgPercentChange / maxPercentChange) * BigDecimal.valueOf(
                    0.5
                ))
            }.take(5)
    }

    override fun getAllStockSymbols(): List<String?> {
        return db.asSequence().map { it.value.symbol }.distinct().toList()
    }
}
