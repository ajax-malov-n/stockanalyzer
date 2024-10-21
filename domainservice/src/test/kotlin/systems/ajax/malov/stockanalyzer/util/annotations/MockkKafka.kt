package systems.ajax.malov.stockanalyzer.util.annotations

import com.ninjasquad.springmockk.MockkBean
import systems.ajax.malov.stockanalyzer.kafka.configuration.consumer.KafkaConsumerConfiguration
import systems.ajax.malov.stockanalyzer.kafka.configuration.producer.KafkaProducerConfiguration
import systems.ajax.malov.stockanalyzer.kafka.processor.StockPriceNotificationProcessor
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceNotificationProducer

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MockkBean(
    relaxed = true,
    classes = [
        StockPriceNotificationProcessor::class,
        KafkaConsumerConfiguration::class,
        KafkaProducerConfiguration::class,
        StockPriceKafkaProducer::class,
        StockPriceNotificationProducer::class,
    ]
)
annotation class MockkKafka
