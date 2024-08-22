package systems.ajax.malov.stockanalyzer.mapper

import StockFixture.aggregatedStockResponseDto
import StockFixture.notAggregatedResponseForFiveBestStocks
import org.junit.jupiter.api.Test
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockResponseDtoMapper.toAggregatedStockItemResponseDto
import kotlin.test.assertEquals

class AggregatedStockResponseDtoMapperTest {

    @Test
    fun `should map list of pair of string and list stocks to aggregatedStockResponseDtoMapper`() {
        val expected = aggregatedStockResponseDto()

        val actual = toAggregatedStockItemResponseDto(notAggregatedResponseForFiveBestStocks())

        assertEquals(expected, actual)
    }
}
