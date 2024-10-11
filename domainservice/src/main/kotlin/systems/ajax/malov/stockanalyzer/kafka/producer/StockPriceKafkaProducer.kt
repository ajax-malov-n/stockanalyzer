package systems.ajax.malov.stockanalyzer.kafka.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toFlux
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.stock_price.proto.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.StockPriceMapper.toStockPrice
import java.time.Duration

@Component
class StockPriceKafkaProducer(
    private val kafkaStockPriceKafkaProducer: KafkaSender<String, StockPrice>,
) {
    fun sendStockPrice(mongoStockRecords: List<MongoStockRecord>): Mono<Unit> {
        return mongoStockRecords.map { it.toStockPrice() }
            .toFlux()
            .buffer(Duration.ofSeconds(1))
            .flatMap {
                kafkaStockPriceKafkaProducer.send(
                    it.map { stockPrice -> buildKafkaStockPriceMessage(stockPrice) }
                        .toFlux()
                )
            }
            .takeLast(1)
            .singleOrEmpty()
            .map { }
    }

    private fun buildKafkaStockPriceMessage(stockPrice: StockPrice): SenderRecord<String, StockPrice, Nothing?> {
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE,
                stockPrice.stockSymbolName,
                stockPrice
            ),
            null
        )
    }
}
