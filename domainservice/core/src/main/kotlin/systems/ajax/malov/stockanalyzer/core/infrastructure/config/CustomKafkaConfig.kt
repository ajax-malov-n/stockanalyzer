package systems.ajax.malov.stockanalyzer.core.infrastructure.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.notifier.KafkaGlobalExceptionHandler

@Configuration
class CustomKafkaConfig {

    @Bean
    fun kafkaGlobalExceptionHandler(): KafkaGlobalExceptionHandler {
        return object : KafkaGlobalExceptionHandler {
            override fun doOnError(kafkaEvent: KafkaEvent<*>, ex: Throwable): Mono<Unit> {
                log.error(ex.message, ex)
                return Unit.toMono()
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomKafkaConfig::class.java)
    }
}
