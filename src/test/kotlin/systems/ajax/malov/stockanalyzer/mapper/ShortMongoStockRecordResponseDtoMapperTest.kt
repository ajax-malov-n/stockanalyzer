package systems.ajax.malov.stockanalyzer.mapper

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.stockanalyzer.dto.ShortStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.mapper.ShortStockRecordResponseDtoMapper.toShortStockRecordResponseDto

class ShortMongoStockRecordResponseDtoMapperTest {

    @Test
    fun `should map stock to shortStockResponseDto`() {
        val expected = ShortStockRecordResponseDto(
            BigDecimal("1.0"),
            BigDecimal("1.0"),
            BigDecimal("1.0"),
            BigDecimal("1.0"),
        )

        val actual = savedStockRecord().toShortStockRecordResponseDto()

        assertEquals(expected, actual)
    }
}
