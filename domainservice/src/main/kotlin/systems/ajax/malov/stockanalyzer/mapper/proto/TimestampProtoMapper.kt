package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.Timestamp
import java.time.Instant

object TimestampProtoMapper {
    fun Instant.toTimestampProto(): Timestamp {
        return Timestamp.newBuilder()
            .setSeconds(epochSecond)
            .setNanos(nano)
            .build()
    }
}
