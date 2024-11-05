package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.producer

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher
import systems.ajax.malov.internalapi.KafkaTopic
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.mapper.StockRecordMapper.toStockPrice

@Component
class StockPriceKafkaProducer(
    private val kafkaPublisher: KafkaPublisher,
) : systems.ajax.malov.stockanalyzer.stockrecord.application.port.out.StockPriceProducerOutPort {
    override fun sendStockPrice(mongoStockRecords: List<StockRecord>): Mono<Unit> {
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
