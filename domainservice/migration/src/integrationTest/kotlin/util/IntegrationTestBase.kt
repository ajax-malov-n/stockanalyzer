package util

import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import systems.ajax.malov.stockanalyzer.core.infrastructure.config.MongoConfig

@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings("UnnecessaryAbstractClass")
@Import(IntegrationTestBase.TestMongoConfig::class)
@ContextConfiguration(
    classes = [
        MongoConfig::class,
        MongoDataAutoConfiguration::class,
        MongoAutoConfiguration::class,
        MongoDatabaseFactory::class,
    ]
)
@ComponentScan(
    basePackages = ["systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.entity"]
)
abstract class IntegrationTestBase {
    class TestMongoConfig {
        @Bean
        fun mongoTemplate(mongoProperties: MongoProperties): MongoTemplate {
            val databaseFactory =
                SimpleMongoClientDatabaseFactory(mongoProperties.uri)
            return MongoTemplate(databaseFactory)
        }
    }
}
