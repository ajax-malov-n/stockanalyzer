package systems.ajax.malov.stockanalyzer.config.beanpostprocessor

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import java.lang.reflect.Proxy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import kotlin.test.Test

class LogExecutionTimeAnnotationBeanPostProcessorTest {

    private lateinit var beanPostProcessor: LogExecutionTimeAnnotationBeanPostProcessor
    private lateinit var logAppender: TestLogAppender

    @BeforeEach
    fun setup() {
        beanPostProcessor = LogExecutionTimeAnnotationBeanPostProcessor()

        logAppender = TestLogAppender()
        logAppender.start()

        val logger = LoggerFactory.getLogger(LogExecutionTimeAnnotationBeanPostProcessor::class.java) as Logger
        logger.addAppender(logAppender)
    }

    @Test
    fun `should return proxy when bean has annotated methods`() {
        // GIVEN
        val bean = AnnotatedBean()
        val beanName = "annotatedBean"
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)

        // WHEN
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName)

        // THEN
        assertNotNull(proxy)
        assertTrue(Proxy.isProxyClass(proxy?.javaClass), "Expected true because bean must be proxied")
    }

    @Test
    fun `should not return proxy when bean has no annotated methods`() {
        // GIVEN
        val bean = Bean()
        val beanName = "annotatedBean"
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)

        // WHEN
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName)

        // THEN
        assertNotNull(proxy)
        assertFalse(Proxy.isProxyClass(proxy?.javaClass), "Expected false because bean must not be proxied")
    }

    @Test
    fun `should log execution time when invoking annotated method`() {
        // GIVEN
        val bean = AnnotatedBean()
        val beanName = "annotatedBean"
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName) as IBean

        // WHEN
        proxy.annotatedMethod("1")
        proxy.method()

        // THEN
        val logEvent = logAppender.events.firstOrNull()
        assertThat(logEvent).isNotNull
        assertThat(logEvent?.level.toString()).isEqualTo(System.Logger.Level.INFO.toString())
        assertThat(logEvent.toString()).contains("Execution time of annotatedMethod is")
    }

    @Test
    fun `should not log execution time when invoking not annotated method`() {
        // GIVEN
        val bean = AnnotatedBean()
        val beanName = "annotatedBean"
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName) as IBean

        // WHEN
        proxy.method()

        // THEN
        val logEvent = logAppender.events.firstOrNull()
        assertThat(logEvent).isNull()
    }

    private class TestLogAppender : AppenderBase<ILoggingEvent>() {
        val events: MutableList<ILoggingEvent> = mutableListOf()

        override fun append(eventObject: ILoggingEvent) {
            events.add(eventObject)
        }
    }

    private interface IBean {
        fun annotatedMethod(s: String)

        fun method()
    }

    private class AnnotatedBean : IBean {
        @LogExecutionTime
        override fun annotatedMethod(s: String) {
            println("Doing something")
        }

        override fun method() {
            println("Doing something")
        }
    }

    private class Bean : IBean {
        override fun annotatedMethod(s: String) {
            println("Doing something")
        }

        override fun method() {
            println("Doing something")
        }
    }
}
