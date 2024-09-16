package systems.ajax.malov.stockanalyzer.repository.impl

import com.mongodb.client.model.Filters
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import stockanalyzer.utils.StockFixture.alsoFirstPlaceStockRecord
import stockanalyzer.utils.StockFixture.firstPlaceStockRecord
import stockanalyzer.utils.StockFixture.secondPlaceStockRecord
import stockanalyzer.utils.StockFixture.unsavedStockRecord
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest

class MongoStockRecordRepositoryTest : AbstractMongoIntegrationTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var mongoStockRecordRepository: MongoStockRecordRepository

    @AfterEach
    fun tearDown() {
        mongoTemplate.getCollection(MongoStockRecord.COLLECTION_NAME)
            .deleteMany(Filters.empty())
    }

    @Test
    fun `insertAll fun inserts all stocks records and retrieves inserted stock records ids`() {
        val listOfUnsavedStockRecords = listOf(unsavedStockRecord())

        val actual = mongoStockRecordRepository.insertAll(listOfUnsavedStockRecords)

        assertEquals(listOfUnsavedStockRecords.size, actual.size)
        assertEquals(listOfUnsavedStockRecords.size, actual.count { it.id != null })
    }

    @Test
    fun `getAllStockSymbols retrieves all stock symbols`() {
        val listOfUnsavedStocks = listOf(unsavedStockRecord())
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)

        val actual = mongoStockRecordRepository.findAllStockSymbols()

        assertEquals(listOfUnsavedStocks.map { it.symbol }.toList(), actual)
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords retrieves five best stocks symbols with stock records`() {
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

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5)

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

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5)

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
            percentChange = null
        )
        val listOfUnsavedStocks = listOf(nullStock)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
        val expected = mapOf(
            nullStock.symbol to listOf(nullStock)
        )

        // WHEN
        val actual = mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5)

        // THEN
        assertEquals(expected.size, actual.size)
        assertEquals(expected.keys, actual.keys)
    }
}
