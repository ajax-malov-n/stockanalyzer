package systems.ajax.malov.stockrecord.infrastructure.nats.stock

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.stockrecord.application.port.out.StockMessageOutPort
import systems.ajax.malov.stockrecord.domain.StockPrice
import systems.ajax.malov.stockrecord.infrastructure.nats.mapper.StockPriceMapper.toProto
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Service
class NatsStockMessageHandler(
    private val publisher: NatsMessagePublisher,
) : StockMessageOutPort {
    override fun publishStockPrice(stockPrice: StockPrice): Mono<Unit> {
        return publisher.publish(
            NatsSubject.StockRequest.getStockPriceSubject(stockPrice.stockSymbolName),
            stockPrice.toProto()
        ).toMono()
    }
}
