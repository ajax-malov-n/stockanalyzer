package systems.ajax.malov.stockanalyzer.mapper

import io.finnhub.api.models.Quote
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.math.BigDecimal
import java.time.Instant

object QuoteMapper {
    fun Quote.toStockRecord(
        scrSymbol: String,
        dateOfRetrievalSrc: Instant,
    ) = MongoStockRecord(
        id = null,
        symbol = scrSymbol,
        openPrice = o?.let { BigDecimal.valueOf(it.toDouble()) },
        highPrice = h?.let { BigDecimal.valueOf(it.toDouble()) },
        lowPrice = l?.let { BigDecimal.valueOf(it.toDouble()) },
        currentPrice = c?.let { BigDecimal.valueOf(it.toDouble()) },
        previousClosePrice = pc?.let { BigDecimal.valueOf(it.toDouble()) },
        change = d?.let { BigDecimal.valueOf(it.toDouble()) } ?: BigDecimal.ZERO,
        percentChange = dp?.let { BigDecimal.valueOf(it.toDouble()) } ?: BigDecimal.ZERO,
        dateOfRetrieval = dateOfRetrievalSrc
    )
}
