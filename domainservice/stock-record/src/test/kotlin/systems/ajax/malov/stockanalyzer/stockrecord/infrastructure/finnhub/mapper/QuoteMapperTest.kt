package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.finnhub.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import stockanalyzer.stockrecord.utils.QuoteFixture.quote
import stockanalyzer.stockrecord.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.stockrecord.utils.StockFixture.domainStockRecord
import stockanalyzer.stockrecord.utils.StockFixture.testDate
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.finnhub.mapper.QuoteMapper.toStockRecord
import java.math.BigDecimal

class QuoteMapperTest {

    @Test
    fun `should map quote to stock`() {
        val testDate = testDate()
        val expected = domainStockRecord().copy(dateOfRetrieval = testDate)

        val actual = quote().toStockRecord(TEST_STOCK_SYMBOL, testDate)

        assertEquals(expected, actual)
    }

    @Test
    fun `should map quote with change and percentChange as null values to stock`() {
        val testDate = testDate()
        val expected = domainStockRecord().copy(change = BigDecimal.ZERO, percentChange = BigDecimal.ZERO)
            .copy(dateOfRetrieval = testDate)

        val actual = quote().copy(d = null, dp = null).toStockRecord(TEST_STOCK_SYMBOL, testDate)

        assertEquals(expected, actual)
    }
}
