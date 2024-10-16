package systems.ajax.malov.stockanalyzer.kafka.configuration.consumer

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice
import systems.ajax.malov.stockanalyzer.config.BaseKafkaConfiguration

@Configuration
class KafkaConsumerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") val bootstrapServer: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String,
    kafkaProperties: KafkaProperties,
) : BaseKafkaConfiguration(bootstrapServer, schemaRegistryUrl, kafkaProperties) {

    @Bean
    fun kafkaStockPriceReceiver(): KafkaReceiver<String, StockPrice> {
        val customProperties: MutableMap<String, Any> = mutableMapOf(
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to StockPrice::class.java.name
        )
        return createKafkaReceiver(
            baseConsumerProperties(customProperties),
            KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE,
            CONSUMER_STOCK_PRICE_GROUP
        )
    }

    companion object {
        private const val CONSUMER_STOCK_PRICE_GROUP = "stockPriceConsumerGroup"
    }
}
