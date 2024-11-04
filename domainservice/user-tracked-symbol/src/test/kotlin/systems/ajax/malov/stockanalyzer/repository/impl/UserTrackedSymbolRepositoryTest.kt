//package systems.ajax.malov.stockanalyzer.repository.impl
//
//import org.bson.types.ObjectId
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.data.mongodb.core.aggregation.Fields
//import org.springframework.data.mongodb.core.exists
//import org.springframework.data.mongodb.core.query.Criteria
//import org.springframework.data.mongodb.core.query.Query
//import org.springframework.data.mongodb.core.query.isEqualTo
//import reactor.kotlin.test.test
//import stockanalyzer.utils.UserTrackedSymbolFixture.mongoUserTrackedSymbol
//import systems.ajax.malov.stockanalyzer.util.IntegrationTestBase
//import systems.ajax.malov.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol
//import java.math.BigDecimal
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertFalse
//
//class UserTrackedSymbolRepositoryTest : IntegrationTestBase() {
//
//    @Autowired
//    private lateinit var userTrackedSymbolRepository: UserTrackedSymbolRepository
//
//    @Autowired
//    private lateinit var mongoTemplate: MongoTemplate
//
//    @Test
//    fun `should return user tracked symbol`() {
//        // GIVEN
//        val mongoUserTrackedStock = mongoUserTrackedSymbol()
//        val saved = mongoTemplate.insert(mongoUserTrackedStock)
//
//        // WHEN
//        val actual = userTrackedSymbolRepository.findUserIdsToNotify(
//            mongoUserTrackedStock.stockSymbolName!!,
//            BigDecimal("2")
//        )
//
//        // THEN
//        actual.test()
//            .assertNext {
//                assertEquals(saved.id, it.id)
//            }
//            .verifyComplete()
//
//        mongoTemplate.remove(saved, MongoUserTrackedSymbol.COLLECTION_NAME)
//    }
//
//    @Test
//    fun `should delete user tracked symbol`() {
//        // GIVEN
//        val mongoUserTrackedStock = mongoUserTrackedSymbol()
//            .copy(id = ObjectId("6706a8343faaa9b224585591"))
//
//        mongoTemplate.insert(mongoUserTrackedStock)
//
//        // WHEN
//        val actual = userTrackedSymbolRepository.deleteUserTrackedSymbol(listOf(mongoUserTrackedStock.id!!))
//
//        // THEN
//        actual.test()
//            .expectNext(Unit)
//            .verifyComplete()
//
//        assertFalse(
//            mongoTemplate.exists<MongoUserTrackedSymbol>(
//                Query.query(
//                    Criteria.where(Fields.UNDERSCORE_ID)
//                        .isEqualTo(mongoUserTrackedStock.id)
//                )
//            ),
//            "User tracked symbol must be not found after deletion"
//        )
//    }
//}
