package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import com.google.protobuf.GeneratedMessageV3
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController

@Component
class NatsControllerBeanPostProcessor : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is NatsController<*, *>) {
            initializeController(bean)
        }
        return bean
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> initializeController(
        controller: NatsController<RequestT, ResponseT>,
    ) {
        // Handle error
        controller.connection.createDispatcher { message ->
            Mono.fromSupplier {
                controller.parser.parseFrom(message.data)
            }.flatMap {
                controller.handle(it)
            }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    controller.connection.publish(message.replyTo, it.toByteArray())
                }
        }.subscribe(controller.subject, controller.queueGroup)
    }
}
