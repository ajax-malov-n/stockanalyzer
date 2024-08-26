package systems.ajax.malov.stockanalyzer.mapper

import org.junit.jupiter.api.Test
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockResponseDtoMapper.toAggregatedStockItemResponseDto
import utils.StockFixture.aggregatedStockResponseDto
import utils.StockFixture.notAggregatedResponseForFiveBestStocks
import kotlin.test.assertEquals

class AggregatedStockResponseDtoMapperTest {

    @Test
    fun `should map list of pair of string and list stocks to aggregatedStockResponseDtoMapper`() {
        val expected = aggregatedStockResponseDto()

        val actual = toAggregatedStockItemResponseDto(notAggregatedResponseForFiveBestStocks())

        assertEquals(expected, actual)
    }
}
