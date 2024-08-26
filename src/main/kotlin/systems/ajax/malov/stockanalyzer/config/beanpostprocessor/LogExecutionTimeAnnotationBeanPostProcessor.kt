package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy


@ConditionalOnProperty(name = ["logExecutionTime.enabled"], havingValue = "true")
@Component
class LogExecutionTimeAnnotationBeanPostProcessor : BeanPostProcessor {

    private val beanMap = HashMap<String, Pair<Class<*>, List<Method>>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val methods: List<Method> = bean.javaClass.methods.filter {
            it.isAnnotationPresent(LogExecutionTime::class.java)
        }
        if (methods.isNotEmpty()) {
            beanMap[beanName] = bean.javaClass to methods
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beanMap[beanName]?.let { (beanClass, methods) ->
            Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                getAllInterfaces(beanClass)
            ) { _, proxiedMethod, args ->
                if (isRequiredProxy(methods, proxiedMethod)) {
                    logMethodExecution(proxiedMethod, bean, args)
                } else {
                    proxiedMethod.invoke(bean, *(args ?: emptyArray()))
                }
            }
        } ?: bean
    }

    private fun isRequiredProxy(
        methods: List<Method>,
        proxiedMethod: Method,
    ) = methods.any { originalMethod ->
        areMethodsEqual(
            originalMethod,
            proxiedMethod
        )
    }

    private fun logMethodExecution(
        proxiedMethod: Method,
        bean: Any,
        args: Array<out Any>?,
    ): Any? {
        val before = System.nanoTime()
        return try {
            proxiedMethod.invoke(bean, *(args ?: emptyArray<Any?>()))
        } finally {
            val executionTimeInMs = (System.nanoTime() - before) / 1_000_000
            log.info("Execution time of {} is {} ms", proxiedMethod.name, executionTimeInMs)
        }
    }

    private fun getAllInterfaces(clazz: Class<*>): Array<Class<*>> {
        return generateSequence(clazz) { it.superclass }
            .flatMap { it.interfaces.asSequence() }
            .toSet()
            .toTypedArray()
    }

    private fun areMethodsEqual(originalMethod: Method, proxiedMethod: Method): Boolean {
        return originalMethod.name == proxiedMethod.name &&
                originalMethod.returnType == proxiedMethod.returnType &&
                originalMethod.parameterTypes.contentEquals(proxiedMethod.parameterTypes)
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogExecutionTimeAnnotationBeanPostProcessor::class.java)
    }
}
