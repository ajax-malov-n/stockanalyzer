package systems.ajax.malov.gateway.infrastructure.mapper

import com.google.protobuf.ByteString
import systems.ajax.malov.commonmodel.stock.BigDecimalProto
import systems.ajax.malov.commonmodel.stock.BigIntegerProto
import systems.ajax.malov.commonmodel.stock.ShortStockRecord
import systems.ajax.malov.gateway.infrastructure.dto.ShortStockRecordResponseDto
import java.math.BigDecimal
import java.math.BigInteger

object ShortStockRecordResponseDtoMapper {
    fun ShortStockRecord.toShortStockRecordResponseDto() =
        ShortStockRecordResponseDto(
            convertBigDecimalProtoToBigDecimal(lowPrice),
            convertBigDecimalProtoToBigDecimal(highPrice),
            convertBigDecimalProtoToBigDecimal(currentPrice),
            convertBigDecimalProtoToBigDecimal(openPrice)
        )

    private fun convertToBigIntegerProto(message: BigIntegerProto): BigInteger {
        val bytes: ByteString = message.value
        return BigInteger(bytes.toByteArray())
    }

    private fun convertBigDecimalProtoToBigDecimal(message: BigDecimalProto): BigDecimal {
        val bigInt = convertToBigIntegerProto(message.intVal)
        return BigDecimal(bigInt, message.scale)
    }
}
