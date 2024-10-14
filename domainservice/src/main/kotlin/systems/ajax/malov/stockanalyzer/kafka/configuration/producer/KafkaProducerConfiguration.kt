package systems.ajax.malov.stockanalyzer.kafka.configuration.producer

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender
import systems.ajax.malov.internalapi.output.pubsub.stock.notification_stock_price.proto.NotificationStockPrice
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.config.BaseKafkaConfiguration

@Configuration
class KafkaProducerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String,
    kafkaProperties: KafkaProperties,
) : BaseKafkaConfiguration(bootstrapServers, schemaRegistryUrl, kafkaProperties) {

    @Bean
    fun kafkaStockPriceSender(): KafkaSender<String, StockPrice> {
        return createKafkaSender(baseProducerProperties())
    }

    @Bean
    fun kafkaNotificationStockPriceSender(): KafkaSender<String, NotificationStockPrice> {
        return createKafkaSender(baseProducerProperties())
    }
}
