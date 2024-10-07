package systems.ajax.malov.gateway.mapper

import com.google.protobuf.ByteString
import systems.ajax.malov.gateway.dto.ShortStockRecordResponseDto
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BInteger
import java.math.BigDecimal
import java.math.BigInteger
import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse as protoShortStockDto

object ShortStockRecordResponseDtoMapper {
    fun protoShortStockDto.toShortStockRecordResponseDto() =
        ShortStockRecordResponseDto(
            convertBDecimalToBigDecimal(lowPrice),
            convertBDecimalToBigDecimal(highPrice),
            convertBDecimalToBigDecimal(currentPrice),
            convertBDecimalToBigDecimal(openPrice)
        )

    private fun convertBIntegerToBigInteger(message: BInteger): BigInteger {
        val bytes: ByteString = message.value
        return BigInteger(bytes.toByteArray())
    }

    private fun convertBDecimalToBigDecimal(message: BDecimal): BigDecimal {
        val bigInt = convertBIntegerToBigInteger(message.intVal)
        return BigDecimal(bigInt, message.scale)
    }
}
