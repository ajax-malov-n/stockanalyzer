package systems.ajax.malov.stockanalyzer.mongock.changelogs

import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import systems.ajax.malov.stockanalyzer.config.NatsDispatcherConfig
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.NatsControllerBeanPostProcessor
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.kafka.configuration.consumer.KafkaConsumerConfiguration
import systems.ajax.malov.stockanalyzer.kafka.configuration.producer.KafkaProducerConfiguration
import systems.ajax.malov.stockanalyzer.kafka.processor.StockPriceNotificationProcessor
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceNotificationProducer
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import kotlin.test.Test
import kotlin.test.assertNull

@SpringBootTest
@MockBean(
    value = [
        Connection::class,
        NatsControllerBeanPostProcessor::class,
        StockPriceNotificationProcessor::class,
        KafkaConsumerConfiguration::class,
        KafkaProducerConfiguration::class,
        StockPriceKafkaProducer::class,
        NatsDispatcherConfig::class,
        StockPriceNotificationProducer::class,
    ]
)
class UserTrackedSymbolCollectionIndexesMigrationTest : AbstractMongoIntegrationTest {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val userTrackedSymbolCollectionIndexesMigration = lazy {
        UserTrackedSymbolCollectionIndexesMigration(mongoTemplate)
    }

    @Test
    fun `should create compound index on stockSymbolName and thresholdPrice`() {
        // GIVEN WHEN
        userTrackedSymbolCollectionIndexesMigration.value.createUserTrackedSymbolCollectionIndex(mongoTemplate)

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
        userTrackedSymbolCollectionIndexesMigration.value.createUserTrackedSymbolCollectionIndex(mongoTemplate)

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
