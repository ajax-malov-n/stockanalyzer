package systems.ajax.malov.stockanalyzer.mapper

import StockFixture.savedStock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import systems.ajax.malov.stockanalyzer.dto.ShortStockResponseDto
import systems.ajax.malov.stockanalyzer.mapper.ShortStockResponseDtoMapper.toShortStockResponseDto
import java.math.BigDecimal

class ShortStockResponseDtoMapperTest {

    @Test
    fun `should map stock to shortStockResponseDto`() {
        val expected = ShortStockResponseDto(
            BigDecimal("1.0"),
            BigDecimal("1.0"),
            BigDecimal("1.0"),
            BigDecimal("1.0"),
        )

        val actual = savedStock().toShortStockResponseDto()

        assertEquals(expected, actual)
    }
}
