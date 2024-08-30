package systems.ajax.malov.stockanalyzer.repository.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import stockanalyzer.utils.StockFixture.alsoFirstPlaceStockRecord
import stockanalyzer.utils.StockFixture.firstPlaceStockRecord
import stockanalyzer.utils.StockFixture.secondPlaceStockRecord
import stockanalyzer.utils.StockFixture.unsavedStockRecord

@ExtendWith(MockitoExtension::class)
class StockRecordInMemoryRepositoryTest {

    @InjectMocks
    private lateinit var stockRecordRepository: InMemoryStockRecordRepositoryImpl

    @Test
    fun `insertAll fun inserts all stocks records and retrieves inserted stock records with ids`() {
        val listOfUnsavedStockRecords = listOf(unsavedStockRecord())

        val actual = stockRecordRepository.insertAll(listOfUnsavedStockRecords)

        assertEquals(listOfUnsavedStockRecords.size, actual.size)
        assertEquals(listOfUnsavedStockRecords.size, actual.count { it.id != null })
        assertEquals(listOfUnsavedStockRecords.map { it.symbol }, actual.map { it.symbol })
    }

    @Test
    fun `getAllStockSymbols retrieves all stock symbols from db`() {
        val listOfUnsavedStocks = listOf(unsavedStockRecord())
        stockRecordRepository.insertAll(listOf(unsavedStockRecord()))

        val actual = stockRecordRepository.findAllStockSymbols()

        assertEquals(listOfUnsavedStocks.map { it.symbol }.toSet(), actual)
    }

    @Test
    fun `findTopNStockSymbolsWithStockRecords retrieves five best stocks symbols with stock records from db`() {
        //GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val secondBestStock = secondPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        stockRecordRepository.insertAll(listOfUnsavedStocks)
        val expected = mapOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            secondBestStock.symbol to listOf(secondBestStock)
        )

        //WHEN
        val actual = stockRecordRepository.findTopNStockSymbolsWithStockRecords(5)

        //THEN
        assertEquals(expected.size, actual.size)
        assertEquals(expected.keys, actual.keys)
    }
}
