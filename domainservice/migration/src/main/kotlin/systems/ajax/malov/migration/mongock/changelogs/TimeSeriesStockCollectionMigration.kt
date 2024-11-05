package systems.ajax.malov.migration.mongock.changelogs

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.timeseries.Granularity
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.entity.MongoStockRecord

@ChangeUnit(id = "CreateTimeSeriesStockCollectionChangelog", order = "001", author = "malov.n@ajax.com")
class TimeSeriesStockCollectionMigration(private val mongoTemplate: MongoTemplate) {

    @Execution
    fun createTimeSeriesStockCollection() {
        if (!mongoTemplate.collectionExists(MongoStockRecord.COLLECTION_NAME)) {
            val timeSeriesOptions = CollectionOptions
                .TimeSeriesOptions
                .timeSeries(MongoStockRecord::dateOfRetrieval.name)
                .metaField(MongoStockRecord::symbol.name)
                .granularity(Granularity.MINUTES)
            val options: CollectionOptions = CollectionOptions.empty().timeSeries(timeSeriesOptions)

            mongoTemplate.createCollection(MongoStockRecord.COLLECTION_NAME, options)
        }
    }

    @RollbackExecution
    fun rollback() {
        log.info("Rolling back ${this::class.simpleName}")

        mongoTemplate.dropCollection(MongoStockRecord.COLLECTION_NAME)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TimeSeriesStockCollectionMigration::class.java)
    }
}
