package systems.ajax.malov.stockanalyzer.mapper

import org.junit.jupiter.api.Test
import systems.ajax.malov.stockanalyzer.mapper.QuoteMapper.toStock
import utils.QuoteFixture.quote
import utils.StockFixture.TEST_STOCK_SYMBOL
import utils.StockFixture.testDate
import utils.StockFixture.unsavedStock
import kotlin.test.assertEquals

class QuoteMapperTest {

    @Test
    fun `should map quote to stock`() {
        val expected = unsavedStock()

        val actual = quote().toStock(TEST_STOCK_SYMBOL, testDate())

        assertEquals(expected, actual);
    }
}
