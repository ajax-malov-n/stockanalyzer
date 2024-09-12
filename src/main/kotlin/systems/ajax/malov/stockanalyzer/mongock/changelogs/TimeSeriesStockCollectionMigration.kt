package systems.ajax.malov.stockanalyzer.mongock.changelogs

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.timeseries.Granularity


@ChangeUnit(id = "CreateTimeSeriesStockCollectionChangelog", order = "1", author = "malov.n@ajax.com")
class TimeSeriesStockCollectionMigration {

    @Execution
    fun createTimeSeriesStockCollection(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists("stockRecords")) {
            val timeSeriesOptions = CollectionOptions
                .TimeSeriesOptions
                .timeSeries("dateOfRetrieval")
                .metaField("symbol")
                .granularity(Granularity.MINUTES)
            val options: CollectionOptions = CollectionOptions.empty().timeSeries(timeSeriesOptions);

            // Create the time series collection
            mongoTemplate.createCollection("stockRecords", options)
        }
    }

    @RollbackExecution
    fun rollback() {
        log.info("Rollback for CreateTimeSeriesStockCollectionChangelog migration is not implemented")
    }

    companion object {
        private val log = LoggerFactory.getLogger(TimeSeriesStockCollectionMigration::class.java)
    }
}

