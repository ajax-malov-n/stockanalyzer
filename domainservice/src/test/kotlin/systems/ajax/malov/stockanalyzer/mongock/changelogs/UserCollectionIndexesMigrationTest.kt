package systems.ajax.malov.stockanalyzer.mongock.changelogs

import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import systems.ajax.malov.stockanalyzer.entity.MongoUser
import systems.ajax.malov.stockanalyzer.util.IntegrationTestBase
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserCollectionIndexesMigrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val userCollectionIndexesMigration = lazy {
        UserCollectionIndexesMigration(mongoTemplate)
    }

    @Test
    fun `should create unique index on email field`() {
        // GIVEN WHEN
        userCollectionIndexesMigration.value.createUserCollectionIndex()

        // THEN
        val indexInfoList = mongoTemplate.indexOps(MongoUser::class.java).indexInfo
        val indexInfo = indexInfoList.find { it.name == "${MongoUser::email.name}_1" }

        assertNotNull(indexInfo, "Index on email should exist")
        assertTrue(indexInfo!!.isUnique, "Index on email should be unique")
    }

    @Test
    fun `should rollback index creation`() {
        // GIVEN
        userCollectionIndexesMigration.value.createUserCollectionIndex()

        // WHEN
        userCollectionIndexesMigration.value.rollback()

        // THEN
        val indexInfoList = mongoTemplate.indexOps(MongoUser::class.java).indexInfo
        val indexInfo = indexInfoList.find { it.name == "${MongoUser::email.name}_1" }

        assertNull(indexInfo, "Index on email should have been dropped")
    }
}
