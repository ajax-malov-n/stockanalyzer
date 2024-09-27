package systems.ajax.malov.stockanalyzer.repository.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import stockanalyzer.utils.StockFixture.alsoFirstPlaceStockRecord
import stockanalyzer.utils.StockFixture.firstPlaceStockRecord
import stockanalyzer.utils.StockFixture.secondPlaceStockRecord
import stockanalyzer.utils.StockFixture.testDate
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.assertNotNull

class MongoStockRecordRepositoryTest : AbstractMongoIntegrationTest {
    @Autowired
    private lateinit var mongoStockRecordRepository: MongoStockRecordRepository

    @Test
    fun `insertAll fun inserts all stocks records and retrieves inserted stock records ids`() {
        val expected = listOf(unsavedStockRecord())

        val actual = mongoStockRecordRepository.insertAll(expected)

        actual.test()
            .assertNext {
                assertNotNull(expected[0].id, "Id must not be null after insertion")
            }
            .verifyComplete()
    }

    @Test
    fun `getAllStockSymbols retrieves all stock symbols`() {
        val listOfUnsavedStocks = listOf(unsavedStockRecord().copy(symbol = "AJAX"))
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .subscribe()

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
    fun `findTopNStockSymbolsWithStockRecords retrieves N best stocks symbols with stock records`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val secondBestStock = secondPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .subscribe()
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
    fun `findTopNStockSymbolsWithStockRecords retrieves fiveBestStocksSymbols with stockRecords with nulls`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val secondBestStock = secondPlaceStockRecord().copy(
            symbol = null,
            openPrice = null,
            highPrice = null,
            lowPrice = null,
            currentPrice = null,
            previousClosePrice = null,
            change = null,
            percentChange = null
        )
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .subscribe()
        val expected = mapOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            "Not provided" to listOf(secondBestStock)
        )
        val from = Date.from(testDate().minus(1, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(1, ChronoUnit.DAYS))

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
    fun `findTopNStockSymbolsWithStockRecords retrieves stockRecords even if we have stock with all nulls`() {
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
            .subscribe()
        val from = Date.from(testDate().plus(3, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(5, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5, from, to)

        // THEN
        actual.test()
            .verifyComplete()
    }
}
