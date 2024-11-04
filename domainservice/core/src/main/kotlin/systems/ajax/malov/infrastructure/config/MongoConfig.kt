package systems.ajax.malov.infrastructure.config

import io.mongock.runner.springboot.EnableMongock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.math.BigDecimal

@Configuration
@EnableMongock
class MongoConfig {
    @Bean
    @Primary
    fun mongoManagedTypes(applicationContext: org.springframework.context.ApplicationContext): org.springframework.data.mongodb.MongoManagedTypes {
        return org.springframework.data.mongodb.MongoManagedTypes.fromIterable(
            org.springframework.boot.autoconfigure.domain.EntityScanner(
                applicationContext
            ).scan(org.springframework.data.mongodb.core.mapping.Document::class.java)
        )
    }

    @Bean
    @SuppressWarnings("ObjectLiteralToLambda")
    fun mongoCustomConversions(): org.springframework.data.mongodb.core.convert.MongoCustomConversions {
        val converters = listOf<org.springframework.core.convert.converter.Converter<*, *>>(
            object : org.springframework.core.convert.converter.Converter<BigDecimal, org.bson.types.Decimal128> {
                override fun convert(source: BigDecimal): org.bson.types.Decimal128 {
                    return org.bson.types.Decimal128(source)
                }
            },
            object : org.springframework.core.convert.converter.Converter<org.bson.types.Decimal128, BigDecimal> {
                override fun convert(source: org.bson.types.Decimal128): BigDecimal {
                    return source.bigDecimalValue()
                }
            }
        )

        return org.springframework.data.mongodb.core.convert.MongoCustomConversions(converters)
    }
}
