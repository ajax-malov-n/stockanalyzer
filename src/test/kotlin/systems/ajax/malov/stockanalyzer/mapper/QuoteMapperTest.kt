package systems.ajax.malov.stockanalyzer.mapper

import java.math.BigDecimal
import org.junit.jupiter.api.Test
import stockanalyzer.utils.QuoteFixture.quote
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.testDate
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStockRecord
import kotlin.test.assertEquals

class QuoteMapperTest {

    @Test
    fun `should map quote to stock`() {
        val expected = unsavedStockRecord()

        val actual = quote().toStockRecord(TEST_STOCK_SYMBOL, testDate())

        assertEquals(expected, actual);
    }

    @Test
    fun `should map quote with change and percentChange as null values to stock`() {
        val expected = unsavedStockRecord().copy(change = BigDecimal.ZERO, percentChange = BigDecimal.ZERO)

        val actual = quote().copy(d = null, dp = null).toStockRecord(TEST_STOCK_SYMBOL, testDate())

        assertEquals(expected, actual);
    }
}
