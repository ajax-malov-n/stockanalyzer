package systems.ajax.malov.stockanalyzer.repository.impl

import org.springframework.stereotype.Repository
import systems.ajax.malov.stockanalyzer.constant.NUMBER_OF_HISTORY_RECORDS_PER_STOCK
import systems.ajax.malov.stockanalyzer.constant.WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.repository.StockRepository
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.reflect.KProperty1


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

    override fun findTopNStockSymbolsWithStockData(n: Int): List<Pair<String, List<Stock>>> {
        if (db.isEmpty()) return emptyList()

        val maxPercentChange = getMaxBigDecimal(Stock::percentChange)
        val maxChange = getMaxBigDecimal(Stock::change)

        val timeOfRequest = Instant.now()

        return db.asSequence()
            .filterNot { (_, stock) -> stock.symbol == null }
            .filter { (_, stock) ->
                isStockInValidDateRange(stock, timeOfRequest)
            }
            .map { it.value }
            .groupBy { it.symbol!! }
            .toList()
            .sortedByDescending(selectorFunctionToCompareStocksByPrice(maxChange, maxPercentChange))
            .take(n)
            .map { (symbol, stocks) ->
                symbol to stocks.distinctBy { it.currentPrice }
                    .sortedByDescending { stock -> stock.dateOfRetrieval }
                    .take(NUMBER_OF_HISTORY_RECORDS_PER_STOCK)
            }
            .toList()
    }

    private fun selectorFunctionToCompareStocksByPrice(
        maxChange: BigDecimal,
        maxPercentChange: BigDecimal,
    ): (Pair<String, List<Stock>>) -> BigDecimal {
        return { pair: Pair<String, List<Stock>> ->
            val stockList = pair.second
            val avgChange = getAvgOfBigDecimals(stockList) { stock: Stock -> stock.change }
            val avgPercentChange = getAvgOfBigDecimals(stockList) { stock: Stock -> stock.percentChange }

            val priceChangeCoefficient =
                (avgChange / maxChange) * BigDecimal.valueOf(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS)
            val pricePercentChangeCoefficient =
                (avgPercentChange / maxPercentChange) * BigDecimal.valueOf(WEIGHTED_COEFFICIENT_FOR_PRICE_COEFFICIENTS)
            priceChangeCoefficient + pricePercentChangeCoefficient
        }
    }

    private fun isStockInValidDateRange(stock: Stock, timeOfRequest: Instant): Boolean {
        return stock.dateOfRetrieval
            ?.isBefore(
                timeOfRequest.plus(Duration.ofHours(1))
            ) == true
    }

    private fun getMaxBigDecimal(property: KProperty1<Stock, BigDecimal?>): BigDecimal {
        return db
            .mapNotNull { (_, stock) -> property.get(stock) }
            .maxBy { it }
    }

    private fun getAvgOfBigDecimals(
        stockList: List<Stock>,
        mapperToBigDecimal: (stock: Stock) -> BigDecimal?,
    ): BigDecimal {
        return stockList
            .mapNotNull { mapperToBigDecimal(it) }
            .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc + bigDecimal } /
                BigDecimal.valueOf(stockList.size.toLong())
    }

    override fun findAllStockSymbols(): List<String> {
        return db
            .mapNotNull { it.value.symbol }
            .distinct()
            .toList()
    }
}
