package systems.ajax.malov.stockanalyzer.core.shared.mapper.proto

import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertBigDecimalProtoToBigDecimal
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalProtoMapperTest {
    @Test
    fun `should convert BigDecimalProto to BigDecimal`() {
        // Given
        val bigDecimalProto = convertToBigDecimalProto(BigDecimal("1"))

        // When
        val actual = convertBigDecimalProtoToBigDecimal(bigDecimalProto)

        // Then
        val expected = BigDecimal.ONE
        assertEquals(expected, actual)
    }
}
