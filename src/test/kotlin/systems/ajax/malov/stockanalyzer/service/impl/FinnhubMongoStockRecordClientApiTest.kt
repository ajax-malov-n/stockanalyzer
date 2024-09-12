package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.Quote
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.unsavedStockRecord


@ExtendWith(MockitoExtension::class)
class FinnhubMongoStockRecordClientApiTest {

    @Mock
    private lateinit var finnhubStockApi: DefaultApi

    @InjectMocks
    private lateinit var finnhubStockRecordClientApi: FinnhubStockRecordClientApi

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(finnhubStockRecordClientApi, "symbols", listOf(TEST_STOCK_SYMBOL))
    }

    @Test
    fun `getAllStockRecords calls Finnhub API and retrieves stock records`() {
        val expected = listOf(unsavedStockRecord())
        val quote = Quote(1f, 1f, 1f, 1f, 1f, 1f, 1f)
        whenever(finnhubStockApi.quote(TEST_STOCK_SYMBOL))
            .thenReturn(quote)

        val actual = finnhubStockRecordClientApi.getAllStockRecords()

        verify(finnhubStockApi).quote(eq(TEST_STOCK_SYMBOL))
        assertEquals(expected.size, actual.size)
        assertEquals(expected[0].symbol, actual[0].symbol)
    }
}
