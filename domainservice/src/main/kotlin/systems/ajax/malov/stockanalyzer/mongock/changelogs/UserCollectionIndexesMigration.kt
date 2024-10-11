package systems.ajax.malov.stockanalyzer.mongock.changelogs

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.indexOps
import systems.ajax.malov.stockanalyzer.entity.MongoUser

@ChangeUnit(id = "CreateUserCollectionIndexesChangelog", order = "2", author = "malov.n@ajax.com")
class UserCollectionIndexesMigration(private val mongoTemplate: MongoTemplate) {
    private lateinit var indexName: String

    @Execution
    fun createUserCollectionIndex(mongoTemplate: MongoTemplate) {
        mongoTemplate.indexOps<MongoUser>().apply {
            indexName = ensureIndex(Index(MongoUser::email.name, ASC).unique())
        }
    }

    @RollbackExecution
    fun rollback() {
        log.info("Rolling back ${this::class.simpleName}")

        mongoTemplate.indexOps<MongoUser>().dropIndex(indexName)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserCollectionIndexesMigration::class.java)
    }
}
