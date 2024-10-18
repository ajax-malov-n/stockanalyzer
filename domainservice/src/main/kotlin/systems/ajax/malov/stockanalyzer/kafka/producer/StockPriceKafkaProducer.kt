package systems.ajax.malov.stockanalyzer.kafka.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.StockPriceMapper.toStockPrice

@Component
class StockPriceKafkaProducer(
    private val kafkaStockPriceSender: KafkaSender<String, ByteArray>,
) {
    fun sendStockPrice(mongoStockRecords: List<MongoStockRecord>): Mono<Unit> {
        return kafkaStockPriceSender.send(
            mongoStockRecords.map { it.toStockPrice() }
                .map { stockPrice -> buildKafkaStockPriceMessage(stockPrice) }
                .toFlux()
        )
            .then(Unit.toMono())
    }

    private fun buildKafkaStockPriceMessage(stockPrice: StockPrice): SenderRecord<String, ByteArray, Nothing?> {
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE,
                stockPrice.stockSymbolName,
                stockPrice.toByteArray()
            ),
            null
        )
    }
}
