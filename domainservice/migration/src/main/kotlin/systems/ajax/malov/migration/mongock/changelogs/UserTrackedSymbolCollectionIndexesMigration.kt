package systems.ajax.malov.migration.mongock.changelogs

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.indexOps
import systems.ajax.malov.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol

@ChangeUnit(id = "CreateUserTrackedSymbolCollectionIndexesChangelog", order = "003", author = "malov.n@ajax.com")
class UserTrackedSymbolCollectionIndexesMigration(private val mongoTemplate: MongoTemplate) {

    @Execution
    fun createUserTrackedSymbolCollectionIndex() {
        val stockSymbolNameAndThresholdPriceIdx = Index()
            .named(INDEX_NAME)
            .on(MongoUserTrackedSymbol::stockSymbolName.name, Sort.Direction.ASC)
            .on(MongoUserTrackedSymbol::thresholdPrice.name, Sort.Direction.ASC)
        mongoTemplate.indexOps<MongoUserTrackedSymbol>().ensureIndex(stockSymbolNameAndThresholdPriceIdx)
    }

    @RollbackExecution
    fun rollback() {
        log.info("Rolling back ${this::class.simpleName}")

        mongoTemplate.indexOps<MongoUserTrackedSymbol>().dropIndex(INDEX_NAME)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserTrackedSymbolCollectionIndexesMigration::class.java)
        private val INDEX_NAME =
            "${MongoUserTrackedSymbol::stockSymbolName.name}_1_${MongoUserTrackedSymbol::thresholdPrice.name}_1"
    }
}
