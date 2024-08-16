package systems.ajax.malov.stockanalyzer.entity

import io.finnhub.api.models.Quote
import java.time.Instant
import java.util.UUID

//@Document(collection = "stocks")
data class Stock (
//    @Id
    var id: UUID? = null,
    val symbol: String?,
    val openPrice: Float?,
    val highPrice: Float?,
    val lowPrice: Float?,
    val currentPrice: Float?,
    val previousClosePrice: Float?,
    val change: Float?,
    val percentChange: Float?,
    val dateOfRetrieval: Instant?
) {
    companion object {
        fun fromQuote(
            scrSymbol: String,
            src: Quote,dateOfRetrievalSrc: Instant
        ) = Stock(
            symbol = scrSymbol,
            openPrice = src.o,
            highPrice = src.h,
            lowPrice = src.l,
            currentPrice = src.c,
            previousClosePrice = src.pc,
            change = src.d,
            percentChange = src.dp,
            dateOfRetrieval = dateOfRetrievalSrc)
    }
}
