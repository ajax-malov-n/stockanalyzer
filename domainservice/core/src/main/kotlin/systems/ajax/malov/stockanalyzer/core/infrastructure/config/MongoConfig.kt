package systems.ajax.malov.stockanalyzer.core.infrastructure.config

import org.springframework.boot.autoconfigure.domain.EntityScanner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.MongoManagedTypes
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Configuration
class MongoConfig {
    @Bean
    @Primary
    fun mongoManagedTypes(applicationContext: ApplicationContext): MongoManagedTypes {
        return MongoManagedTypes.fromIterable(
            EntityScanner(
                applicationContext
            ).scan(Document::class.java)
        )
    }

    @Bean
    @SuppressWarnings("ObjectLiteralToLambda")
    fun mongoCustomConversions(): MongoCustomConversions {
        val converters = listOf<Converter<*, *>>(
            object : Converter<BigDecimal, org.bson.types.Decimal128> {
                override fun convert(source: BigDecimal): org.bson.types.Decimal128 {
                    return org.bson.types.Decimal128(source)
                }
            },
            object : Converter<org.bson.types.Decimal128, BigDecimal> {
                override fun convert(source: org.bson.types.Decimal128): BigDecimal {
                    return source.bigDecimalValue()
                }
            }
        )

        return MongoCustomConversions(converters)
    }
}
