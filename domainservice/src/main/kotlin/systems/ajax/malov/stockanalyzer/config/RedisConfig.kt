package systems.ajax.malov.stockanalyzer.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord

@Configuration
@EnableCaching
class RedisConfig {
    @Bean
    fun reactiveStringRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, String> {
        val context = RedisSerializationContext
            .newSerializationContext<String, String>(StringRedisSerializer())
            .value(StringRedisSerializer())
            .build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }

    @Bean
    fun reactiveMapRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Map<String, List<MongoStockRecord>>> {
        val objectMapper = ObjectMapper().findAndRegisterModules().registerKotlinModule()

        val typeFactory: TypeFactory = objectMapper.typeFactory
        val keyType = typeFactory.constructType(String::class.java)
        val listType = typeFactory.constructCollectionType(List::class.java, MongoStockRecord::class.java)
        val mapType = typeFactory.constructMapType(Map::class.java, keyType, listType)

        val jacksonSerializer = Jackson2JsonRedisSerializer<Map<String, List<MongoStockRecord>>>(objectMapper, mapType)

        val context = RedisSerializationContext
            .newSerializationContext<String, Map<String, List<MongoStockRecord>>>(
                RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer)
            )
            .value(RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer))
            .build()

        return ReactiveRedisTemplate(connectionFactory, context)
    }
}
