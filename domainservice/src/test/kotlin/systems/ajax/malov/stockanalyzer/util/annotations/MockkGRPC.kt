package systems.ajax.malov.stockanalyzer.util.annotations

import com.ninjasquad.springmockk.MockkBean
import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MockkBean(
    relaxed = true,
    classes = [
        GrpcServerAutoConfiguration::class,
        GrpcServerFactoryAutoConfiguration::class,
        GrpcClientAutoConfiguration::class
    ]
)
annotation class MockkGRPC
