package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import com.google.protobuf.GeneratedMessageV3
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController

@Component
class NatsControllerBeanPostProcessor(private val dispatcher: Dispatcher) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is NatsController<*, *>) {
            initializeController(bean)
        }
        return bean
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> initializeController(
        controller: NatsController<RequestT, ResponseT>,
    ) {
        val handler = MessageHandler { message ->
            Mono.fromSupplier {
                controller.parser.parseFrom(message.data)
            }.flatMap {
                controller.handle(it)
            }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    controller.connection.publish(message.replyTo, it.toByteArray())
                }
        }
        dispatcher.subscribe(controller.subject, controller.queueGroup, handler)
    }
}
