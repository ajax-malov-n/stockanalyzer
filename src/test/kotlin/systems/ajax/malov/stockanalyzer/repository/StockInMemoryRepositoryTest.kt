package systems.ajax.malov.stockanalyzer.repository

import StockFixture.alsoFirstPlaceStock
import StockFixture.firstPlaceStock
import StockFixture.secondPlaceStock
import StockFixture.unsavedStock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import systems.ajax.malov.stockanalyzer.repository.impl.StockInMemoryRepositoryImpl

@ExtendWith(MockitoExtension::class)
class StockInMemoryRepositoryTest {

    @InjectMocks
    private lateinit var stockRepository: StockInMemoryRepositoryImpl

    @Test
    fun `insertAll fun inserts all records and retrieves inserted records with ids`() {
        val listOfUnsavedStocks = listOf(unsavedStock())

        val actual = stockRepository.insertAll(listOfUnsavedStocks)

        assertEquals(listOfUnsavedStocks.size, actual.size)
        assertEquals(listOfUnsavedStocks.size, actual.count { it.id != null })
        assertEquals(listOfUnsavedStocks.map { it.symbol }, actual.map { it.symbol })
    }

    @Test
    fun `getAllStockSymbols fun retrieves all stock symbols from db`() {
        val listOfUnsavedStocks = listOf(unsavedStock())
        stockRepository.insertAll(listOf(unsavedStock()))

        val actual = stockRepository.getAllStockSymbols()

        assertEquals(listOfUnsavedStocks.map { it.symbol }, actual)
    }

    @Test
    fun `findFiveBestStocksToBuy fun retrieves five best stocks from db`() {
        //GIVEN
        val bestStock1 = firstPlaceStock()
        val bestStock2 = alsoFirstPlaceStock()
        val secondBestStock = secondPlaceStock()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        stockRepository.insertAll(listOfUnsavedStocks)
        val expected = listOf(
            bestStock1.symbol to listOf(bestStock1, bestStock2),
            secondBestStock.symbol to listOf(secondBestStock)
        )

        //WHEN
        val actual = stockRepository.findFiveBestStocksToBuy()

        //THEN
        assertEquals(expected.size, actual.size)
        assertEquals(expected[0].first, actual[0].first)
        assertEquals(expected[0].second.size, actual[0].second.size)
        assertEquals(expected[1].first, actual[1].first)
        assertEquals(expected[1].second.size, actual[1].second.size)
    }
}
