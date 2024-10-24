package systems.ajax.malov.stockanalyzer.kafka.consumer

import io.nats.client.Connection
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice

@Component
class StockPriceNatsConsumer(
    private val kafkaStockPriceNatsReceiver: KafkaReceiver<String, ByteArray>,
    private val nastConnection: Connection,
) {
    @PostConstruct
    fun subscribeToEvents() {
        kafkaStockPriceNatsReceiver.receiveBatch()
            .flatMap { stockPriceConsumerRecords ->
                stockPriceConsumerRecords.flatMap { record ->
                    handle(StockPrice.parseFrom(record.value()))
                        .doFinally { record.receiverOffset().acknowledge() }
                }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun handle(stockPrice: StockPrice): Mono<Unit> {
        return nastConnection.publish(
            NatsSubject.StockRequest.getStockPriceSubject(stockPrice.stockSymbolName), stockPrice.toByteArray()
        ).toMono()
    }
}
