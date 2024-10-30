package systems.ajax.malov.gateway.client

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.internalapi.NatsSubject

@Component
class NatsClient(
    private val natsConnection: Connection,
    private val dispatcher: Dispatcher,
) {

    fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): Mono<ResponseT> {
        return Mono.fromFuture {
            natsConnection.request(
                subject,
                payload.toByteArray()
            )
        }.map {
            parser.parseFrom(it.data)
        }
    }

    fun subscribeByStockSymbolName(stockSymbolName: String): Flux<StockPrice> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<StockPrice>()
        val subscription =
            dispatcher.subscribe(NatsSubject.StockRequest.getStockPriceSubject(stockSymbolName)) { message ->
                sink.tryEmitNext(StockPrice.parseFrom(message.data))
            }
        return sink.asFlux()
            .doFinally {
                log.info("NATS Finalizing subscription to $stockSymbolName")
                dispatcher.unsubscribe(subscription)
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NatsClient::class.java)
    }
}
