package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class LogExecutionTimeAnnotationBeanPostProcessor : BeanPostProcessor {
    private val beanMap = HashMap<String, Any>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean.javaClass.methods.any { it.isAnnotationPresent(LogExecutionTime::class.java) }) {
            beanMap[beanName] = bean
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beanMap[beanName]?.let { beanClass ->
            Proxy.newProxyInstance(
                beanClass.javaClass.getClassLoader(),
                getAllInterfaces(beanClass.javaClass).toTypedArray()
            )
            { _, method, args ->
                if (isMethodAnnotatedInSuperclass(method, beanClass)) {
                    val before = System.nanoTime()
                    val result = method.invoke(bean, *(args ?: emptyArray()))
                    log.info("Execution time of ${method.name} is ${(System.nanoTime() - before) / 1_000_000} ms")
                    result
                } else {
                    method.invoke(bean, *(args ?: emptyArray()))
                }
            }
        } ?: bean
    }

    private fun getAllInterfaces(clazz: Class<*>): Set<Class<*>> {
        val interfaces = mutableSetOf<Class<*>>()
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            interfaces.addAll(currentClass.interfaces)
            currentClass = currentClass.superclass
        }
        return interfaces
    }

    private fun isMethodAnnotatedInSuperclass(method: Method, bean: Any): Boolean {
        val clazz: Class<*> = bean.javaClass
        try {
            val superMethod = clazz.getDeclaredMethod(method.name, *(method.parameterTypes ?: emptyArray()))
            return superMethod.isAnnotationPresent(LogExecutionTime::class.java)
        } catch (_: NoSuchMethodException) {
            return false
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogExecutionTimeAnnotationBeanPostProcessor::class.java)
    }
}
