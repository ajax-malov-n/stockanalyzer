package systems.ajax.malov.stockanalyzer.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponseDto
import systems.ajax.malov.stockanalyzer.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto

class AggregatedStockRecordItemResponseDtoMapperTest {
    @Test
    fun `should map stock records list with a common stock symbol to aggregatedStockRecordItemResponseDto`() {
        val stocks = listOf(savedStockRecord())
        val expected =
            AggregatedStockRecordItemResponseDto(TEST_STOCK_SYMBOL, stocks.map { it.toShortStockRecordResponseDto() })

        val actual = stocks.toAggregatedStockRecordItemResponseDto(TEST_STOCK_SYMBOL)

        assertEquals(expected, actual)
    }
}
