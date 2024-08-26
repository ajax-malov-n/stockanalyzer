package systems.ajax.malov.stockanalyzer.repository.impl

import org.springframework.stereotype.Repository
import systems.ajax.malov.stockanalyzer.constant.NUMBER_OF_HISTORY_RECORDS_PER_STOCK
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.*


@Repository
class StockInMemoryRepositoryImpl : StockRepository {
    private val db: HashMap<UUID, Stock> = HashMap()

    override fun insertAll(stocks: List<Stock>): List<Stock> {
        stocks.forEach {
            val id = UUID.randomUUID()
            it.id = id
            db[id] = it
        }
        return stocks
    }

    override fun findFiveBestStocksToBuy(): List<Pair<String?, List<Stock>>> {
        if (db.isEmpty()) return emptyList()

        val maxPercentChange = getMaxBigDecimal { stock: Stock -> stock.percentChange }
        val maxChange = getMaxBigDecimal { stock: Stock -> stock.change }

        val timeOfRequest = Instant.now()

        return db.asSequence().filterNot { (_, stock) -> stock.symbol == null }
            .filter { (_, stock) ->
                stock.dateOfRetrieval?.isBefore(timeOfRequest.plus(Duration.ofHours(1))) == true
            }.map { it.value }.groupBy { it.symbol }.toList().sortedByDescending { (_, stockList) ->
                val avgChange = getAvgOfBigDecimals(stockList) { stock: Stock -> stock.change }
                val avgPercentChange = getAvgOfBigDecimals(stockList) { stock: Stock -> stock.percentChange }

                val firstCoefficient = (avgChange / maxChange) * BigDecimal.valueOf(0.5)
                val secondCoefficient = (avgPercentChange / maxPercentChange) * BigDecimal.valueOf(0.5)
                firstCoefficient + secondCoefficient
            }
            .take(5)
            .map { (symbol, stocks) ->
                symbol to stocks.distinctBy { it.currentPrice }
                    .sortedByDescending { stock -> stock.dateOfRetrieval }
                    .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK)
            }
            .toList()
    }

    private fun getMaxBigDecimal(mapperToBigDecimal: (stock: Stock) -> BigDecimal?): BigDecimal {
        return db
            .mapNotNull { (_, stock) -> mapperToBigDecimal(stock) }
            .maxBy { it }
    }

    private fun getAvgOfBigDecimals(
        stockList: List<Stock>,
        mapperToBigDecimal: (stock: Stock) -> BigDecimal?,
    ): BigDecimal {
        return stockList.mapNotNull { mapperToBigDecimal(it) }
            .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc + bigDecimal } /
                BigDecimal.valueOf(stockList.size.toLong())
    }

    override fun getAllStockSymbols(): List<String?> {
        return db.map { it.value.symbol }.distinct().toList()
    }
}
