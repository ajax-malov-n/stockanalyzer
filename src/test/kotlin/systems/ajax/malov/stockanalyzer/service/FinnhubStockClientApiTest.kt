package systems.ajax.malov.stockanalyzer.service

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.Quote
import io.finnhub.api.models.StockSymbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import systems.ajax.malov.stockanalyzer.constant.EXCHANGE_NAME
import systems.ajax.malov.stockanalyzer.service.impl.FinnhubStockClientApi
import systems.ajax.malov.stockanalyzer.utils.TEST_STOCK_SYMBOL
import systems.ajax.malov.stockanalyzer.utils.unsavedStock

@ExtendWith(MockitoExtension::class)
class FinnhubStockClientApiTest {

    @Mock
    private lateinit var finnhubStockApi: DefaultApi

    @InjectMocks
    private lateinit var finnhubStockClientApi: FinnhubStockClientApi

    @Test
    fun `getAllStocksData fun calls Finnhub API and retrieves stocks`(){
        val expected = listOf(unsavedStock)
        val quote = Quote(1f, 1f, 1f, 1f, 1f, 1f, 1f)
        val stockSymbols = listOf(StockSymbol(displaySymbol = TEST_STOCK_SYMBOL))
        whenever(finnhubStockApi.stockSymbols(EXCHANGE_NAME,"","",""))
            .thenReturn(stockSymbols)
        whenever(finnhubStockApi.quote(TEST_STOCK_SYMBOL)).thenReturn(quote)

        val actual = finnhubStockClientApi.getAllStocksData()

        verify(finnhubStockApi).stockSymbols(eq(EXCHANGE_NAME), eq(""),eq(""),eq(""))
        verify(finnhubStockApi).quote(eq(TEST_STOCK_SYMBOL))
        assertEquals(expected.size, actual.size)
        assertEquals(expected[0].symbol, actual[0].symbol)
    }
}
