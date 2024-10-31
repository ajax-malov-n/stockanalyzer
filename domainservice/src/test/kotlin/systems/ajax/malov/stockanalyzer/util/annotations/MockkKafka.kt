package systems.ajax.malov.stockanalyzer.util.annotations

import com.ninjasquad.springmockk.MockkBean
import systems.ajax.malov.stockanalyzer.kafka.consumer.StockPriceNatsConsumer
import systems.ajax.malov.stockanalyzer.kafka.processor.StockPriceNotificationProcessor
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceKafkaProducer
import systems.ajax.malov.stockanalyzer.kafka.producer.StockPriceNotificationProducer

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MockkBean(
    relaxed = true,
    classes = [
        StockPriceNotificationProcessor::class,
        StockPriceKafkaProducer::class,
        StockPriceNotificationProducer::class,
        StockPriceNatsConsumer::class,
    ]
)
annotation class MockkKafka
