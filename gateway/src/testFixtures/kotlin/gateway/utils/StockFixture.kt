/*
package gateway.utils

import java.time.Clock
import java.time.Instant
import java.time.ZoneId


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    val testDate = Clock.fixed(Instant.now(), ZoneId.of("UTC")).instant()

    fun testDate(): Instant = Clock.fixed(Instant.now(), ZoneId.of("UTC")).instant()

    fun notAggregatedResponseForFiveBestStockSymbolsWithStockRecords() =
        mapOf(TEST_STOCK_SYMBOL to listOf(savedStockRecord()))

    fun aggregatedStockRecordResponseDto() = AggregatedStockRecordResponseDto(
        notAggregatedResponseForFiveBestStockSymbolsWithStockRecords().map {
            AggregatedStockRecordItemResponseDto(
                it.key,
                it.value.map { stock -> stock.toShortStockRecordResponseDto() })
        }
    )
}
*/
