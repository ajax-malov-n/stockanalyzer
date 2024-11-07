package systems.ajax.malov.stockanalyzer.usertrackedsymbol.util

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import systems.ajax.kafka.autoconfiguration.handler.KafkaHandlerAutoConfiguration
import systems.ajax.kafka.autoconfiguration.publisher.KafkaPublisherAutoConfiguration
import systems.ajax.malov.stockanalyzer.core.infrastructure.config.MongoConfig
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.usertrackedsymbol.UserTrackedSymbolRepositoryTest

@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings("UnnecessaryAbstractClass")
@Import(IntegrationTestBase.MyKafkaTestConfiguration::class)
@ContextConfiguration(
    classes = [
        MongoConfig::class,
        MongoReactiveAutoConfiguration::class,
        MongoReactiveDataAutoConfiguration::class,
        UserTrackedSymbolRepositoryTest::class,
        KafkaAutoConfiguration::class,
        KafkaHandlerAutoConfiguration::class,
        KafkaPublisherAutoConfiguration::class
    ]
)
@ComponentScan(
    basePackages = [
        "systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.producer",
        "systems.ajax.malov.stockanalyzer.usertrackedsymbol",
        "systems.ajax.malov.stockanalyzer.core"
    ]
)
abstract class IntegrationTestBase {
    class MyKafkaTestConfiguration {
        @Bean
        fun adminClient(kafkaAdmin: KafkaAdmin): Admin =
            KafkaAdminClient.create(kafkaAdmin.configurationProperties)

        @Bean
        fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, ByteArray> =
            DefaultKafkaConsumerFactory(
                kafkaProperties.buildConsumerProperties(null),
                StringDeserializer(),
                ByteArrayDeserializer()
            )
    }
}
