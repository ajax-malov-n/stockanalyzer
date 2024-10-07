package systems.ajax.malov.stockanalyzer.repository.impl

import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.kotlin.test.test
import stockanalyzer.utils.StockFixture.alsoFirstPlaceStockRecord
import stockanalyzer.utils.StockFixture.firstPlaceStockRecord
import stockanalyzer.utils.StockFixture.secondPlaceStockRecord
import stockanalyzer.utils.StockFixture.testDate
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.NatsControllerBeanPostProcessor
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.assertNotNull

class MongoStockRecordRepositoryTest : AbstractMongoIntegrationTest {
    @MockBean
    @SuppressWarnings("UnusedPrivateProperty")
    private lateinit var natsConnection: Connection

    @MockBean
    @SuppressWarnings("UnusedPrivateProperty")
    private lateinit var natsControllerBeanPostProcessor: NatsControllerBeanPostProcessor

    @Autowired
    private lateinit var mongoStockRecordRepository: MongoStockRecordRepository

    @Test
    fun `insertAll fun should insert all stocks records and retrieve inserted stock records ids`() {
        val expected = listOf(unsavedStockRecord())

        val actual = mongoStockRecordRepository.insertAll(expected)

        actual.test()
            .assertNext {
                assertNotNull(expected[0].id, "Id must not be null after insertion")
            }
            .verifyComplete()
    }

    @Test
    fun `getAllStockSymbols should retrieve all stock symbols`() {
        val listOfUnsavedStocks = listOf(
            unsavedStockRecord()
                .copy(
                    symbol = "AJAX",
                    percentChange = BigDecimal.ZERO
                )
        )
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .blockLast()

        val actual = mongoStockRecordRepository.findAllStockSymbols()

        actual
            .collectList()
            .test()
            .assertNext { result ->
                assertTrue(
                    result
                        .containsAll(
                            listOfUnsavedStocks.map { it.symbol }
                        ),
                    "New inserted stock symbol must be in the response list"
                )
            }
            .verifyComplete()
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords should retrieve N best stocks symbols with stock records`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val secondBestStock = secondPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .blockLast()
        val expected = mapOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            secondBestStock.symbol to listOf(secondBestStock)
        )
        val from = Date.from(testDate().minus(1, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(1, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(2, from, to)

        // THEN
        actual.test()
            .assertNext { result ->
                assertEquals(expected.keys, result.keys)
            }
            .verifyComplete()
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords should retrieve fiveBestStocksSymbols with stockRecords with nulls`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord().copy(
            dateOfRetrieval = testDate()
                .plus(10, ChronoUnit.DAYS)
                .plus(1, ChronoUnit.HOURS)
        )
        val bestStock2 = alsoFirstPlaceStockRecord().copy(
            dateOfRetrieval = testDate()
                .plus(10, ChronoUnit.DAYS)
                .plus(1, ChronoUnit.HOURS)
        )
        val secondBestStock = secondPlaceStockRecord().copy(
            symbol = null,
            openPrice = null,
            highPrice = null,
            lowPrice = null,
            currentPrice = null,
            previousClosePrice = null,
            change = null,
            percentChange = null,
            dateOfRetrieval = testDate()
                .plus(10, ChronoUnit.DAYS)
                .plus(1, ChronoUnit.HOURS)
        )
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .collectList()
            .block()
        val expected = mapOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            "Not provided" to listOf(secondBestStock)
        )
        val from = Date.from(testDate().plus(10, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(11, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5, from, to)

        // THEN
        actual.test()
            .assertNext { result ->
                assertEquals(expected.keys, result.keys)
            }
            .verifyComplete()
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords should retrieve stockRecords even if we have stock with all nulls`() {
        // GIVEN
        val nullStock = secondPlaceStockRecord().copy(
            openPrice = null,
            highPrice = null,
            lowPrice = null,
            currentPrice = null,
            previousClosePrice = null,
            change = null,
            percentChange = null,
            dateOfRetrieval = testDate().plus(4, ChronoUnit.DAYS)
        )
        val listOfUnsavedStocks = listOf(nullStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .collectList()
            .block()
        val from = Date.from(testDate().plus(3, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(5, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5, from, to)

        // THEN
        actual.test()
            .expectNext(linkedMapOf())
            .verifyComplete()
    }
}
