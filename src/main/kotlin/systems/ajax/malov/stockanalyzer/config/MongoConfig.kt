package systems.ajax.malov.stockanalyzer.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.MongoTemplate

//@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {
    @Value("\${mongodb.database}")
    private lateinit var databaseName: String
    @Value("\${mongodb.connection_string}")
    private lateinit var connectionString: String

    override fun getDatabaseName(): String {
        return databaseName;
    }

    override fun mongoClient(): MongoClient {
        return MongoClients.create(connectionString)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), getDatabaseName())
    }
}