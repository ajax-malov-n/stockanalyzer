package systems.ajax.malov.stockanalyzer.kafka.producer

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.StockPriceMapper.toStockPrice

@Component
class StockPriceKafkaProducer(
    private val kafkaPublisher: KafkaPublisher,
) {
    fun sendStockPrice(mongoStockRecords: List<MongoStockRecord>): Mono<Unit> {
        return mongoStockRecords.toFlux()
            .map { it.toStockPrice() }
            .flatMap {
                kafkaPublisher.publish(
                    KafkaTopic.KafkaStockPriceEvents.STOCK_PRICE,
                    it.stockSymbolName,
                    it,
                )
            }.then(Unit.toMono())
    }
}
