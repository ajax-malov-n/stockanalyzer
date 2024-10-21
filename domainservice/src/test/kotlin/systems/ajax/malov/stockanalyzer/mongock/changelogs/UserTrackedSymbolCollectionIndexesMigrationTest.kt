package systems.ajax.malov.stockanalyzer.mongock.changelogs

import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.util.annotations.MockkGRPC
import systems.ajax.malov.stockanalyzer.util.annotations.MockkKafka
import systems.ajax.malov.stockanalyzer.util.annotations.MockkNats
import kotlin.test.Test
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
@MockkGRPC
@MockkNats
@MockkKafka
class UserTrackedSymbolCollectionIndexesMigrationTest {

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
