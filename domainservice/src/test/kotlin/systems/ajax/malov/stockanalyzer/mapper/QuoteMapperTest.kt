package systems.ajax.malov.stockanalyzer.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import stockanalyzer.utils.QuoteFixture.quote
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.testDate
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStockRecord
import java.math.BigDecimal

class QuoteMapperTest {

    @Test
    fun `should map quote to stock`() {
        val testDate = testDate()
        val expected = unsavedStockRecord().copy(dateOfRetrieval = testDate)

        val actual = quote().toStockRecord(TEST_STOCK_SYMBOL, testDate)

        assertEquals(expected, actual)
    }

    @Test
    fun `should map quote with change and percentChange as null values to stock`() {
        val testDate = testDate()
        val expected = unsavedStockRecord().copy(change = BigDecimal.ZERO, percentChange = BigDecimal.ZERO)
            .copy(dateOfRetrieval = testDate)

        val actual = quote().copy(d = null, dp = null).toStockRecord(TEST_STOCK_SYMBOL, testDate)

        assertEquals(expected, actual)
    }
}
