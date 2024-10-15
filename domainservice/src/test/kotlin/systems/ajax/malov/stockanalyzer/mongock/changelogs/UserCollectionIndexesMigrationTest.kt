package systems.ajax.malov.stockanalyzer.mongock.changelogs

import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import systems.ajax.malov.stockanalyzer.config.NatsDispatcherConfig
import systems.ajax.malov.stockanalyzer.config.beanpostprocessor.NatsControllerBeanPostProcessor
import systems.ajax.malov.stockanalyzer.entity.MongoUser
import systems.ajax.malov.stockanalyzer.kafka.configuration.consumer.KafkaConsumerConfiguration
import systems.ajax.malov.stockanalyzer.kafka.configuration.producer.KafkaProducerConfiguration
import systems.ajax.malov.stockanalyzer.kafka.processor.StockPriceNotificationProcessor
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceNotificationProducer
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
class UserCollectionIndexesMigrationTest : AbstractMongoIntegrationTest {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val userCollectionIndexesMigration = lazy {
        UserCollectionIndexesMigration(mongoTemplate)
    }

    @Test
    fun `should create unique index on email field`() {
        // GIVEN WHEN
        userCollectionIndexesMigration.value.createUserCollectionIndex(mongoTemplate)

        // THEN
        val indexInfoList = mongoTemplate.indexOps(MongoUser::class.java).indexInfo
        val indexInfo = indexInfoList.find { it.name == "${MongoUser::email.name}_1" }

        assertNotNull(indexInfo, "Index on email should exist")
        assertTrue(indexInfo!!.isUnique, "Index on email should be unique")
    }

    @Test
    fun `should rollback index creation`() {
        // GIVEN
        userCollectionIndexesMigration.value.createUserCollectionIndex(mongoTemplate)

        // WHEN
        userCollectionIndexesMigration.value.rollback()

        // THEN
        val indexInfoList = mongoTemplate.indexOps(MongoUser::class.java).indexInfo
        val indexInfo = indexInfoList.find { it.name == "${MongoUser::email.name}_1" }

        assertNull(indexInfo, "Index on email should have been dropped")
    }
}
