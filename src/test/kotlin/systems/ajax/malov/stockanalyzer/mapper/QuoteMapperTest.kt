package systems.ajax.malov.stockanalyzer.mapper

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
}
