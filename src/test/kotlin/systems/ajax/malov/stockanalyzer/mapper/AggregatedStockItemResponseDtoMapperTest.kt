package systems.ajax.malov.stockanalyzer.mapper

import StockFixture.TEST_STOCK_SYMBOL
import StockFixture.savedStock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockItemResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.mapper.ShortStockResponseDtoMapper.toShortStockResponseDto

class AggregatedStockItemResponseDtoMapperTest {
    @Test
    fun `should map stocks list with a common stock symbol to aggregatedStockItemResponseDto`() {
        val stocks = listOf(savedStock())
        val expected = AggregatedStockItemResponseDto(TEST_STOCK_SYMBOL, stocks.map { it.toShortStockResponseDto() })

        val actual = stocks.toAggregatedStockItemResponseDto(TEST_STOCK_SYMBOL)

        assertEquals(expected, actual)
    }
}
