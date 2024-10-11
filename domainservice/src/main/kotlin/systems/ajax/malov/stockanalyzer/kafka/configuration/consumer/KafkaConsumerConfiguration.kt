package systems.ajax.malov.stockanalyzer.kafka.configuration.consumer

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.config.BaseKafkaConfiguration

@Configuration
class KafkaConsumerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String,
) : BaseKafkaConfiguration(bootstrapServers, schemaRegistryUrl) {

    @Bean
    fun kafkaStockPriceReceiver(): KafkaReceiver<String, StockPrice> {
        val customProperties: MutableMap<String, Any> = mutableMapOf(
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to StockPrice::class.java.name
        )
        return createKafkaReceiver(
            baseConsumerProperties(customProperties),
            KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE,
            KafkaTopic.STOCK_PRICE_CONSUMER_GROUP
        )
    }
}
