package systems.ajax.malov.stockanalyzer.kafka.configuration.consumer

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.stockanalyzer.config.BaseKafkaConfiguration

@Configuration
class KafkaConsumerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") val bootstrapServer: String,
    kafkaProperties: KafkaProperties,
) : BaseKafkaConfiguration(
    bootstrapServer,
    kafkaProperties
) {

    @Bean
    fun kafkaStockPriceReceiver(): KafkaReceiver<String, ByteArray> {
        return createKafkaReceiver(
            baseConsumerProperties(),
            KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE,
            CONSUMER_STOCK_PRICE_GROUP
        )
    }

    companion object {
        private const val CONSUMER_STOCK_PRICE_GROUP = "stockPriceConsumerGroup"
    }
}
