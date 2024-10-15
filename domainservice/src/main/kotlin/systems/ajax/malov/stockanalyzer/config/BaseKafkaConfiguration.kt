package systems.ajax.malov.stockanalyzer.config

import com.google.protobuf.GeneratedMessageV3
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

open class BaseKafkaConfiguration(
    private val bootstrapServers: String,
    private val schemaRegistryUrl: String,
    private val kafkaProperties: KafkaProperties,
) {
    protected fun <T : GeneratedMessageV3> createKafkaSender(
        properties: MutableMap<String, Any>,
    ): KafkaSender<String, T> {
        return KafkaSender.create(SenderOptions.create(properties))
    }

    protected fun <T : GeneratedMessageV3> createKafkaReceiver(
        properties: MutableMap<String, Any>,
        topic: String,
        groupId: String,
    ): KafkaReceiver<String, T> {
        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        val options = ReceiverOptions.create<String, T>(properties).subscription(setOf(topic))
        return KafkaReceiver.create(options)
    }

    protected fun baseProducerProperties(
        customProperties: Map<String, Any> = mapOf(),
    ): MutableMap<String, Any> {
        val buildProperties: MutableMap<String, Any> = kafkaProperties.producer.buildProperties(null)
        val baseProperties: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )
        buildProperties.putAll(baseProperties)
        buildProperties.putAll(customProperties)
        return buildProperties
    }

    protected fun baseConsumerProperties(
        customProperties: Map<String, Any> = mapOf(),
    ): MutableMap<String, Any> {
        val buildProperties: MutableMap<String, Any> = kafkaProperties.consumer.buildProperties(null)
        val baseProperties: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )
        buildProperties.putAll(baseProperties)
        buildProperties.putAll(customProperties)
        return buildProperties
    }
}
