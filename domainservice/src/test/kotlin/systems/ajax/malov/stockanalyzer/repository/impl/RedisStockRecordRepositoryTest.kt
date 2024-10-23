//package systems.ajax.malov.stockanalyzer.repository.impl
//
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.ActiveProfiles
//import reactor.kotlin.test.test
//import stockanalyzer.utils.StockFixture
//import kotlin.test.assertNotNull
//
//@SpringBootTest
//@ActiveProfiles("test")
//// addHere my custom bean annotations
//class RedisStockRecordRepositoryTest {
//
//    @Autowired
//    private lateinit var redisStockRecordRepository : RedisStockRecordRepository
//
//    @Test
//    fun `findTopStockSymbolsWithStockRecords should retrieve five best stock symbols with records`() {
//        val expected = listOf(StockFixture.unsavedStockRecord())
//
//        val actual =
//            redisStockRecordRepository.findTopStockSymbolsWithStockRecords(5)
//
//        actual.test()
//            .assertNext {
//                assertNotNull(it.id, "Id must not be null after insertion")
//            }
//            .verifyComplete()
//    }
//
//    @Test
//    fun `findAllStockSymbols should retrieve all stock symbols`() {
//        val expected = listOf(StockFixture.unsavedStockRecord())
//
//        val actual =
//            redisStockRecordRepository.findAllStockSymbols()
//
//        actual.test()
//            .assertNext {
//                assertNotNull(it.id, "Id must not be null after insertion")
//            }
//            .verifyComplete()
//    }
//
//    @Test
//    fun `saveTopStockSymbolsWithStockRecords should save all stock symbols`() {
//        val expected = listOf(StockFixture.unsavedStockRecord())
//
//        val actual = redisStockRecordRepository
//            .saveTopStockSymbolsWithStockRecords(expected)
//
//        actual.test()
//            .assertNext {
//                assertNotNull(it.id, "Id must not be null after insertion")
//            }
//            .verifyComplete()
//    }
//
//    @Test
//    fun `saveAllStockSymbols should save best stock symbols with records`() {
//        val expected = listOf(StockFixture.unsavedStockRecord())
//
//        val actual = redisStockRecordRepository
//            .saveTopStockSymbolsWithStockRecords(expected)
//
//        actual.test()
//            .assertNext {
//                assertNotNull(it.id, "Id must not be null after insertion")
//            }
//            .verifyComplete()
//    }
//}
