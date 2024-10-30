package systems.ajax.malov.gateway.config

import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsDispatcherConfig {

    @Bean
    fun natsDispatcher(natsConnection: Connection): Dispatcher = natsConnection.createDispatcher()
}
