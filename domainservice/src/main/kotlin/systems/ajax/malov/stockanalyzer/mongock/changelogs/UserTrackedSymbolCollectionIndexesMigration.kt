package systems.ajax.malov.stockanalyzer.mongock.changelogs

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.indexOps
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol

@ChangeUnit(id = "CreateUserTrackedSymbolCollectionIndexesChangelog", order = "003", author = "malov.n@ajax.com")
class UserTrackedSymbolCollectionIndexesMigration(private val mongoTemplate: MongoTemplate) {
    private lateinit var indexName: String

    @Execution
    fun createUserTrackedSymbolCollectionIndex(mongoTemplate: MongoTemplate) {
        val stockSymbolNameAndThresholdPriceIdx = Index()
            .on(MongoUserTrackedSymbol::stockSymbolName.name, Sort.Direction.ASC)
            .on(MongoUserTrackedSymbol::thresholdPrice.name, Sort.Direction.ASC)
        indexName = mongoTemplate.indexOps<MongoUserTrackedSymbol>().ensureIndex(stockSymbolNameAndThresholdPriceIdx)
    }

    @RollbackExecution
    fun rollback() {
        log.info("Rolling back ${this::class.simpleName}")

        mongoTemplate.indexOps<MongoUserTrackedSymbol>().dropIndex(indexName)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserTrackedSymbolCollectionIndexesMigration::class.java)
    }
}
