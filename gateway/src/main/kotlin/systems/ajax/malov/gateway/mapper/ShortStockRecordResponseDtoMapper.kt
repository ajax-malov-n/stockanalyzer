package systems.ajax.malov.gateway.mapper

import com.google.protobuf.ByteString
import systems.ajax.malov.gateway.dto.ShortStockRecordResponseDto
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigDecimalProto
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigIntegerProto
import java.math.BigDecimal
import java.math.BigInteger
import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse as protoShortStockDto

object ShortStockRecordResponseDtoMapper {
    fun protoShortStockDto.toShortStockRecordResponseDto() =
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
