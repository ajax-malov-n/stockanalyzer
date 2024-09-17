package systems.ajax.malov.stockanalyzer.mapper

import org.junit.jupiter.api.Test
import stockanalyzer.utils.StockFixture.aggregatedStockRecordResponseDto
import stockanalyzer.utils.StockFixture.notAggregatedResponseForFiveBestStockSymbolsWithStockRecords
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import kotlin.test.assertEquals

class AggregatedMongoStockRecordResponseDtoMapperTest {

    @Test
    fun `should map list of pair of string and list stocks to aggregatedStockResponseDtoMapper`() {
        val expected = aggregatedStockRecordResponseDto()

        val actual = toAggregatedStockItemResponseDto(notAggregatedResponseForFiveBestStockSymbolsWithStockRecords())

        assertEquals(expected, actual)
    }
}
