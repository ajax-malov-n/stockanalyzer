package systems.ajax.malov.stockanalyzer.mapper

import QuoteFixture.quote
import StockFixture.TEST_STOCK_SYMBOL
import StockFixture.testDate
import StockFixture.unsavedStock
import org.junit.jupiter.api.Test
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStock
import kotlin.test.assertEquals

class QuoteMapperTest {

    @Test
    fun `should map quote to stock`() {
        val expected = unsavedStock()

        val actual = quote().toStock(TEST_STOCK_SYMBOL, testDate())

        assertEquals(expected, actual);
    }
}
