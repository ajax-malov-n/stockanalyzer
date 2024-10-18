package systems.ajax.malov.stockanalyzer.kafka.configuration.producer

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender
import systems.ajax.malov.stockanalyzer.config.BaseKafkaConfiguration

@Configuration
class KafkaProducerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
    kafkaProperties: KafkaProperties,
) : BaseKafkaConfiguration(
    bootstrapServers,
    kafkaProperties
) {

    @Bean
    fun kafkaStockPriceSender(): KafkaSender<String, ByteArray> {
        return createKafkaSender(baseProducerProperties())
    }

    @Bean
    fun kafkaNotificationStockPriceSender(): KafkaSender<String, ByteArray> {
        return createKafkaSender(baseProducerProperties())
    }
}
