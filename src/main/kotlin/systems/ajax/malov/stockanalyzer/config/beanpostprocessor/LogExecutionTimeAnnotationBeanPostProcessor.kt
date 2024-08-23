package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy


@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class LogExecutionTimeAnnotationBeanPostProcessor : BeanPostProcessor {
    @Value("\${logExecutionTime.enabled}")
    private var logExecutionTimeEnabled: Boolean = false

    private val beanMap = HashMap<String, Pair<Class<*>, List<Method>>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val methods = bean.javaClass.methods.filter { it.isAnnotationPresent(LogExecutionTime::class.java) }
        if (methods.isNotEmpty()) {
            beanMap[beanName] = bean.javaClass to methods
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beanMap[beanName]?.let { (beanClass, methods) ->
            Proxy.newProxyInstance(
                beanClass.getClassLoader(), getAllInterfaces(beanClass).toTypedArray()
            ) { _, method, args ->
                if (logExecutionTimeEnabled && methods.any { areMethodsEqual(it, method) }) {
                    val before = System.nanoTime()
                    val result = method.invoke(bean, *(args ?: emptyArray()))
                    val executionTimeInMs = (System.nanoTime() - before) / 1_000_000
                    log.info("Execution time of {} is {} ms", method.name, executionTimeInMs)
                    result
                } else {
                    method.invoke(bean, *(args ?: emptyArray()))
                }
            }
        } ?: bean
    }

    private fun getAllInterfaces(clazz: Class<*>): Set<Class<*>> {
        return generateSequence(clazz) { it.superclass }
            .flatMap { it.interfaces.asSequence() }
            .toSet()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogExecutionTimeAnnotationBeanPostProcessor::class.java)
    }

    fun areMethodsEqual(method1: Method, method2: Method): Boolean {
        return method1.name == method2.name &&
                method1.returnType == method2.returnType &&
                method1.parameterTypes.contentEquals(method2.parameterTypes)
    }
}
