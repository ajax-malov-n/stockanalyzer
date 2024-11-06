package systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.usertrackedsymbol

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.kotlin.test.test
import stockanalyzer.utils.UserTrackedSymbolFixture.mongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.output.UserTrackedSymbolRepositoryOutPort
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.mapper.UserTrackedSymbolMapper.toDomain
import util.IntegrationTestBase
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UserTrackedSymbolRepositoryTest : IntegrationTestBase() {
    @Autowired
    private lateinit var userTrackedSymbolRepository: UserTrackedSymbolRepositoryOutPort

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun `should return user tracked symbol`() {
        // GIVEN
        val mongoUserTrackedStock = mongoUserTrackedSymbol()
        val saved = reactiveMongoTemplate.insert(mongoUserTrackedStock).block()!!
            .toDomain()

        // WHEN
        val actual = userTrackedSymbolRepository.findUserIdsToNotify(
            mongoUserTrackedStock.stockSymbolName!!,
            BigDecimal("2")
        )

        // THEN
        actual.test()
            .assertNext {
                assertEquals(saved.id, it.id)
            }
            .verifyComplete()

        reactiveMongoTemplate.remove(saved, MongoUserTrackedSymbol.COLLECTION_NAME).block()
    }

    @Test
    fun `should delete user tracked symbol`() {
        // GIVEN
        val mongoUserTrackedStock = mongoUserTrackedSymbol()
            .copy(id = ObjectId("6706a8343faaa9b224585591"))

        reactiveMongoTemplate.insert(mongoUserTrackedStock).block()

        // WHEN
        val actual = userTrackedSymbolRepository.deleteUserTrackedSymbol(listOf(mongoUserTrackedStock.id.toHexString()))

        // THEN
        actual.test()
            .expectNext(Unit)
            .verifyComplete()

        assertFalse(
            reactiveMongoTemplate.exists<MongoUserTrackedSymbol>(
                Query.query(
                    Criteria.where(Fields.UNDERSCORE_ID)
                        .isEqualTo(mongoUserTrackedStock.id)
                )
            ).block()!!,
            "User tracked symbol must be not found after deletion"
        )
    }
}
