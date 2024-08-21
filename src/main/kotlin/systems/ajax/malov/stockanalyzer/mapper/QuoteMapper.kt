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
        openPrice = o?.let { BigDecimal.valueOf(o!!.toDouble()) },
        highPrice = h?.let { BigDecimal.valueOf(h!!.toDouble()) },
        lowPrice = l?.let { BigDecimal.valueOf(l!!.toDouble()) },
        currentPrice = c?.let { BigDecimal.valueOf(c!!.toDouble()) },
        previousClosePrice = pc?.let { BigDecimal.valueOf(pc!!.toDouble()) },
        change = d?.let { BigDecimal.valueOf(d!!.toDouble()) },
        percentChange = dp?.let { BigDecimal.valueOf(dp!!.toDouble()) },
        dateOfRetrieval = dateOfRetrievalSrc
    )
}
