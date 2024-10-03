package systems.ajax.malov.gateway.client

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsClient(private val natsConnection: Connection) {

    fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.request(
            subject,
            payload.toByteArray()
        )
        return parser.parseFrom(response.get().data)
    }
}
