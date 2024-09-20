package systems.ajax.malov.stockanalyzer.repository.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import stockanalyzer.utils.StockFixture.alsoFirstPlaceStockRecord
import stockanalyzer.utils.StockFixture.firstPlaceStockRecord
import stockanalyzer.utils.StockFixture.secondPlaceStockRecord
import stockanalyzer.utils.StockFixture.testDate
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import java.time.temporal.ChronoUnit
import java.util.Date

class MongoStockRecordRepositoryTest : AbstractMongoIntegrationTest {
    @Autowired
    private lateinit var mongoStockRecordRepository: MongoStockRecordRepository

    @Test
    fun `insertAll fun inserts all stocks records and retrieves inserted stock records ids`() {
        val listOfUnsavedStockRecords = listOf(unsavedStockRecord())

        val actual = mongoStockRecordRepository.insertAll(listOfUnsavedStockRecords)

        assertEquals(listOfUnsavedStockRecords.size, actual.size)
        assertEquals(listOfUnsavedStockRecords.size, actual.count { it.id != null })
    }

    @Test
    fun `getAllStockSymbols retrieves all stock symbols`() {
        val listOfUnsavedStocks = listOf(unsavedStockRecord().copy(symbol = "AJAX"))
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)

        val actual = mongoStockRecordRepository.findAllStockSymbols()

        assertTrue(actual.toSet().containsAll(listOfUnsavedStocks.map { it.symbol }))
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords retrieves N best stocks symbols with stock records`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val secondBestStock = secondPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
        val expected = mapOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            secondBestStock.symbol to listOf(secondBestStock)
        )
        val from = Date.from(testDate().minus(1, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(1, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(2, from, to)

        // THEN
        assertEquals(expected.size, actual.size)
        assertEquals(expected.keys, actual.keys)
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords retrieves fiveBestStocksSymbols with stockRecords with nulls`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val secondBestStock = secondPlaceStockRecord().copy(
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
        val expected = mapOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            secondBestStock.symbol to listOf(secondBestStock)
        )
        val from = Date.from(testDate().minus(1, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(1, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5, from, to)

        // THEN
        assertEquals(expected.size, actual.size)
        assertEquals(expected.keys, actual.keys)
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
            dateOfRetrieval = testDate().plus(3, ChronoUnit.DAYS)
        )
        val listOfUnsavedStocks = listOf(nullStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
        val expected = mapOf(
            nullStock.symbol to listOf(nullStock)
        )
        val from = Date.from(testDate().plus(1, ChronoUnit.DAYS))
        val to = Date.from(testDate().plus(4, ChronoUnit.DAYS))

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5, from, to)

        // THEN
        assertEquals(expected.size, actual.size)
        assertEquals(expected.keys, actual.keys)
    }
}
