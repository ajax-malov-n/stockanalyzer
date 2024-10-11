package systems.ajax.malov.stockanalyzer.mapper.proto

import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigDecimalProto
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertBigDecimalProtoToBigDecimal
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalProtoMapperTest {
    @Test
    fun `should convert BigDecimalProto to BigDecimal`() {
        // Given
        val bigDecimalProto: BigDecimalProto = convertToBigDecimalProto(BigDecimal("1"))

        // When
        val actual = convertBigDecimalProtoToBigDecimal(bigDecimalProto)

        // Then
        val expected = BigDecimal("1")
        assertEquals(expected, actual)
    }
}
