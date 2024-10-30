package systems.ajax.malov.stockanalyzer.util.annotations

import com.ninjasquad.springmockk.MockkBean
import io.nats.client.Connection
import io.nats.client.Dispatcher

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MockkBean(
    relaxed = true,
    classes = [
        Connection::class,
        Dispatcher::class,
    ]
)
annotation class MockkNats
