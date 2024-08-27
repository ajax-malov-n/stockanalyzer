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

    private val beanMap = HashMap<String, ClassWithAnnotatedMethods>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val methods: List<Method> = bean.javaClass.methods.filter {
            it.isAnnotationPresent(LogExecutionTime::class.java)
        }
        if (methods.isNotEmpty()) {
            beanMap[beanName] = ClassWithAnnotatedMethods(bean.javaClass, methods)
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beanMap[beanName]?.let { (beanClass, methods) ->
            Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                getAllInterfaces(beanClass)
            ) { _, proxiedMethod, args ->
                if (methods.any { originalMethod -> originalMethod isEqualTo proxiedMethod }) {
                    invokeProxiedMethodWithLogging(proxiedMethod, bean, args)
                } else {
                    proxiedMethod.invoke(bean, *(args ?: emptyArray()))
                }
            }
        } ?: bean
    }

    private fun invokeProxiedMethodWithLogging(
        proxiedMethod: Method,
        bean: Any,
        args: Array<out Any>?,
    ): Any? {
        val before = System.nanoTime()
        return try {
            proxiedMethod.invoke(bean, *(args ?: emptyArray<Any?>()))
        } finally {
            val executionTimeInNs = (System.nanoTime() - before)
            logExecutionTime(proxiedMethod.name, executionTimeInNs)
        }
    }

    private fun getAllInterfaces(clazz: Class<*>): Array<Class<*>> {
        return generateSequence(clazz) { it.superclass }
            .flatMap { it.interfaces.asSequence() }
            .toSet()
            .toTypedArray()
    }

    private infix fun Method.isEqualTo(proxiedMethod: Method): Boolean {
        return this.name == proxiedMethod.name &&
                this.returnType == proxiedMethod.returnType &&
                this.parameterTypes.contentEquals(proxiedMethod.parameterTypes)
    }

    private fun logExecutionTime(proxiedMethodName: String, executionTimeInNs: Long) {
        val seconds = executionTimeInNs / SECONDS
        val remainingNsAfterSeconds = executionTimeInNs % SECONDS
        val milliseconds = remainingNsAfterSeconds / MILLISECONDS
        val nanoseconds = remainingNsAfterSeconds % MILLISECONDS

        val timeStringBuilder = StringBuilder()

        if (seconds > 0) {
            timeStringBuilder.append(seconds).append(" s ")
        }
        if (milliseconds > 0) {
            timeStringBuilder.append(milliseconds).append(" ms ")
        }
        if (nanoseconds > 0) {
            timeStringBuilder.append(nanoseconds).append(" ns ")
        }

        val timeString = if (timeStringBuilder.isEmpty()) {
            "0 ns"
        } else {
            timeStringBuilder.toString().trim()
        }

        log.info("Execution time of {} is {}", proxiedMethodName, timeString)
    }

    companion object {
        const val MILLISECONDS = 1_000_000
        const val SECONDS = 1_000_000_000
        private val log = LoggerFactory.getLogger(LogExecutionTimeAnnotationBeanPostProcessor::class.java)
    }
}

internal data class ClassWithAnnotatedMethods(val clazz: Class<*>, val methods: List<Method>)
