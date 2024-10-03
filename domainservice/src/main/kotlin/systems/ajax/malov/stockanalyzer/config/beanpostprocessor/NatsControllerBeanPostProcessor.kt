package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import com.google.protobuf.GeneratedMessageV3
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
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
        controller.connection.createDispatcher { message ->
            val request = controller.parser.parseFrom(message.data)

            controller.handle(request).subscribe {
                println("LOX")
                controller.connection.publish(message.replyTo, it.toByteArray())
            }
        }.subscribe(controller.subject)
    }
}
