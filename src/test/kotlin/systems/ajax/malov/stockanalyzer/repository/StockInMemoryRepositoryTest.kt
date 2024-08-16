package systems.ajax.malov.stockanalyzer.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import systems.ajax.malov.stockanalyzer.entity.Stock
import systems.ajax.malov.stockanalyzer.repository.impl.StockInMemoryRepositoryImpl
import systems.ajax.malov.stockanalyzer.utils.savedStock
import systems.ajax.malov.stockanalyzer.utils.unsavedStock
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class StockInMemoryRepositoryTest {

    @InjectMocks
    private lateinit var stockRepository: StockInMemoryRepositoryImpl

    @Test
    fun `insertAll fun inserts all records and retrieves inserted records with ids`() {
        val listOfUnsavedStocks = listOf(unsavedStock)

        val actual = stockRepository.insertAll(listOfUnsavedStocks)

        assertEquals(listOfUnsavedStocks.size,actual.size)
        assertEquals(listOfUnsavedStocks.size,actual.count { it.id != null })
        assertEquals(listOfUnsavedStocks.map { it.symbol },actual.map { it.symbol })
    }

    @Test
    fun `getAllStockSymbols fun retrieves all stock symbols from db`() {
        val listOfUnsavedStocks = listOf(unsavedStock)
        stockRepository.insertAll(listOf(unsavedStock))

        val actual = stockRepository.getAllStockSymbols()

        assertEquals(listOfUnsavedStocks.map { it.symbol }, actual)
    }

    @Test
    fun `findFiveBestStocksToBuy fun retrieves five best stocks from db`() {
        val bestStock1 = savedStock.copy(
            symbol = "AAPL",
            change = 2.0f,
            percentChange = 0.3f,
            dateOfRetrieval = Instant.now()
        )
        val bestStock2 = savedStock.copy(
            symbol = "AAPL",
            change = 2.5f,
            percentChange = 0.3f,
            dateOfRetrieval = Instant.now()
        )
        val secondBestStock = savedStock.copy(
            symbol = "SSSK",
            change = 1.0f,
            percentChange = 0.2f,
            dateOfRetrieval = Instant.now()
        )

        val listOfUnsavedStocks = listOf(bestStock1, bestStock2, secondBestStock)
        stockRepository.insertAll(listOfUnsavedStocks)
        val expected = listOf(
            Pair(bestStock1.symbol, listOf(bestStock1, bestStock2)),
            Pair(secondBestStock.symbol, listOf(secondBestStock.symbol))
        )

        val actual = stockRepository.findFiveBestStocksToBuy()

        assertEquals(expected.size, actual.size)
        assertEquals(expected[0].first, actual[0].first)
        assertEquals(expected[0].second.size, actual[0].second.size)
        assertEquals(expected[1].first, actual[1].first)
        assertEquals(expected[1].second.size, actual[1].second.size)
    }
}
