package systems.ajax.malov.stockanalyzer.service.impl

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.Quote
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import stockanalyzer.utils.StockFixture.TEST_STOCK_SYMBOL
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

@ExtendWith(MockKExtension::class)
class FinnhubMongoStockRecordClientApiTest {

    @MockK
    private lateinit var finnhubStockApi: DefaultApi
    private lateinit var finnhubStockRecordClientApi: FinnhubStockRecordClientApi

    @BeforeEach
    fun setup() {
        finnhubStockRecordClientApi = FinnhubStockRecordClientApi(finnhubStockApi, listOf(TEST_STOCK_SYMBOL))
    }

    @Test
    fun `getAllStockRecords calls Finnhub API and retrieves stock records`() {
        // GIVEN
        val expected = listOf(unsavedStockRecord())
        val quote = Quote(1f, 1f, 1f, 1f, 1f, 1f, 1f)
        every {
            finnhubStockApi.quote(TEST_STOCK_SYMBOL)
        } returns quote

        // WHEN
        val actual: Flux<MongoStockRecord> = finnhubStockRecordClientApi.getAllStockRecords()

        // THEN
        StepVerifier.create(actual)
            .expectNextMatches { it.symbol == expected[0].symbol }
            .verifyComplete()
    }
}
