package systems.ajax.malov.stockanalyzer.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.lettuce.core.ClientOptions
import io.lettuce.core.TimeoutOptions
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.timeout.millis}") val timeout: Long,
    @Value("\${spring.data.redis.host}") val host: String,
) {
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host)
        val clientOptions: ClientOptions = ClientOptions.builder()
            .timeoutOptions(
                TimeoutOptions.builder()
                    .timeoutCommands(true)
                    .fixedTimeout(Duration.ofMinutes(timeout))
                    .build()
            ).build()

        return LettuceConnectionFactory(
            config,
            LettuceClientConfiguration.builder().clientOptions(clientOptions).build()
        )
    }

    @Bean
    fun reactiveStringRedisTemplate(
        @Qualifier("reactiveRedisConnectionFactory") connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, String> {
        val context = RedisSerializationContext
            .newSerializationContext<String, String>(StringRedisSerializer())
            .value(StringRedisSerializer())
            .build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }

    @Bean
    fun reactiveMapRedisTemplate(
        @Qualifier("reactiveRedisConnectionFactory") connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Map<String, List<MongoStockRecord>>> {
        val objectMapper = ObjectMapper()
        objectMapper.findAndRegisterModules()
        objectMapper
            .registerKotlinModule()
            .registerModule(JavaTimeModule())

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