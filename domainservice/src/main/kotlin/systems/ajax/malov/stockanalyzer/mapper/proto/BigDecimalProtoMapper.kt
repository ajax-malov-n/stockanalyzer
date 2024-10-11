package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.ByteString
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigDecimalProto
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigIntegerProto
import java.math.BigDecimal
import java.math.BigInteger

object BigDecimalProtoMapper {
    fun convertToBigDecimalProto(bigDecimal: BigDecimal?): BigDecimalProto {
        val scale: Int = bigDecimal?.scale() ?: 0
        return BigDecimalProto.newBuilder()
            .setScale(scale)
            .setIntVal(convertToBigIntegerProto(bigDecimal?.unscaledValue() ?: BigInteger.ZERO))
            .build()
    }

    fun convertBigDecimalProtoToBigDecimal(message: BigDecimalProto): BigDecimal {
        val bigInt = convertBigIntegerProtoToBigInteger(message.intVal)
        return BigDecimal(bigInt, message.scale)
    }

    private fun convertToBigIntegerProto(bigInteger: BigInteger): BigIntegerProto {
        return BigIntegerProto.newBuilder().apply {
            val bytes: ByteString = ByteString.copyFrom(bigInteger.toByteArray())
            setValue(bytes)
        }.build()
    }

    private fun convertBigIntegerProtoToBigInteger(message: BigIntegerProto): BigInteger {
        val bytes: ByteString = message.value
        return BigInteger(bytes.toByteArray())
    }
}
