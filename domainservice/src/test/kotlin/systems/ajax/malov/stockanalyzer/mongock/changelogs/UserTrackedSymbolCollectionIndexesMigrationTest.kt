package systems.ajax.malov.stockanalyzer.mongock.changelogs

import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.util.IntegrationTestBase
import kotlin.test.Test
import kotlin.test.assertNull


class UserTrackedSymbolCollectionIndexesMigrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val userTrackedSymbolCollectionIndexesMigration = lazy {
        UserTrackedSymbolCollectionIndexesMigration(mongoTemplate)
    }

    @Test
    fun `should create compound index on stockSymbolName and thresholdPrice`() {
        // GIVEN WHEN
        userTrackedSymbolCollectionIndexesMigration.value.createUserTrackedSymbolCollectionIndex()

        // THEN
        val indexInfoList = mongoTemplate.indexOps(MongoUserTrackedSymbol::class.java).indexInfo
        val compoundIndexInfo = indexInfoList.find {
            it.name ==
                "${MongoUserTrackedSymbol::stockSymbolName.name}_1_${MongoUserTrackedSymbol::thresholdPrice.name}_1"
        }

        assertNotNull(compoundIndexInfo, "Compound index on stockSymbolName and thresholdPrice should exist")
    }

    @Test
    fun `should rollback compound index creation`() {
        // GIVEN
        userTrackedSymbolCollectionIndexesMigration.value.createUserTrackedSymbolCollectionIndex()

        // WHEN
        userTrackedSymbolCollectionIndexesMigration.value.rollback()

        // THEN
        val indexInfoList = mongoTemplate.indexOps(MongoUserTrackedSymbol::class.java).indexInfo
        val compoundIndexInfo = indexInfoList.find {
            it.name ==
                "${MongoUserTrackedSymbol::stockSymbolName.name}_1_${MongoUserTrackedSymbol::thresholdPrice.name}_1"
        }

        assertNull(compoundIndexInfo, "Compound index on stockSymbolName and thresholdPrice should have been dropped")
    }
}
