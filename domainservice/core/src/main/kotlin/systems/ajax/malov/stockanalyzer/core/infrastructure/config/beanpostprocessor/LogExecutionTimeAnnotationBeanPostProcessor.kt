package systems.ajax.malov.stockanalyzer.core.infrastructure.config.beanpostprocessor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.time.Duration
import java.util.Objects

@ConditionalOnProperty(name = ["logExecutionTime.enabled"], havingValue = "true")
@Component
class LogExecutionTimeAnnotationBeanPostProcessor : BeanPostProcessor {

    private val beanMap = HashMap<String, ClassWithAnnotatedMethods>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val methods: Set<ComparableMethod> = bean.javaClass.methods
            .filter {
                it.isAnnotationPresent(LogExecutionTime::class.java)
            }
            .map { ComparableMethod(it) }
            .toSet()
        if (methods.isNotEmpty()) {
            beanMap[beanName] = ClassWithAnnotatedMethods(bean.javaClass, methods)
        }
        return bean
    }

    @Suppress("SpreadOperator")
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beanMap[beanName]?.let { (beanClass, methods) ->
            Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                getAllInterfaces(beanClass)
            ) { _, proxiedMethod, args ->
                if (methods.contains(ComparableMethod(proxiedMethod))) {
                    invokeProxiedMethodWithLogging(proxiedMethod, bean, args)
                } else {
                    proxiedMethod.invoke(bean, *(args.orEmpty()))
                }
            }
        } ?: bean
    }

    @Suppress("SpreadOperator")
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

    private fun logExecutionTime(proxiedMethodName: String, executionTimeInNs: Long) {
        val duration = Duration.ofNanos(executionTimeInNs)
        val executionTime = StringBuilder().apply {
            append(duration.seconds)
            append(".")
            append(duration.nano)
            append(" ")
            append("s")
        }

        log.info("Execution time of {} is {}", proxiedMethodName, executionTime)
    }

    private data class ComparableMethod(val method: Method) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ComparableMethod) return false
            return method.name == other.method.name && method.returnType == other.method.returnType &&
                method.parameterTypes.contentEquals(other.method.parameterTypes)
        }

        override fun hashCode(): Int {
            return Objects.hash(method.returnType, method.parameterTypes, method.parameterTypes)
        }
    }

    private data class ClassWithAnnotatedMethods(val clazz: Class<*>, val methods: Set<ComparableMethod>)

    companion object {
        private val log = LoggerFactory.getLogger(LogExecutionTimeAnnotationBeanPostProcessor::class.java)
    }
}
