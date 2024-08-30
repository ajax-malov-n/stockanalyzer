package systems.ajax.malov.stockanalyzer.mapper

import io.finnhub.api.models.Quote
import java.math.BigDecimal
import java.time.Instant
import systems.ajax.malov.stockanalyzer.entity.StockRecord

object QuoteMapper {
    fun Quote.toStockRecord(
        scrSymbol: String, dateOfRetrievalSrc: Instant,
    ) = StockRecord(
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
