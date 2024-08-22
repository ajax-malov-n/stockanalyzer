package systems.ajax.malov.stockanalyzer.mapper

import io.finnhub.api.models.Quote
import systems.ajax.malov.stockanalyzer.entity.Stock
import java.math.BigDecimal
import java.time.Instant

object QuoteMapper {
    fun Quote.toStock(
        scrSymbol: String, dateOfRetrievalSrc: Instant,
    ) = Stock(
        symbol = scrSymbol,
        openPrice = o?.let { BigDecimal.valueOf(it.toDouble()) },
        highPrice = h?.let { BigDecimal.valueOf(it.toDouble()) },
        lowPrice = l?.let { BigDecimal.valueOf(it.toDouble()) },
        currentPrice = c?.let { BigDecimal.valueOf(it.toDouble()) },
        previousClosePrice = pc?.let { BigDecimal.valueOf(it.toDouble()) },
        change = d?.let { BigDecimal.valueOf(it.toDouble()) },
        percentChange = dp?.let { BigDecimal.valueOf(it.toDouble()) },
        dateOfRetrieval = dateOfRetrievalSrc
    )
}
