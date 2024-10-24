package systems.ajax.malov.gateway.mapper

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class AggregatedStockRecordResponseDtoMapperMapper {

    @Test
    fun `should throw runtimeException with error message`() {
        // GIVEN
        val protoDto = GetBestStockSymbolsWithStockRecordsResponse.newBuilder()
            .apply {
                failureBuilder.message = "Error"
            }.build()
        // WHEN THEN
        val exception = assertThrows<RuntimeException> { protoDto.toAggregatedStockItemResponseDto() }
        assertEquals("Error", exception.message)
    }

    @Test
    fun `should throw runtimeException`() {
        // GIVEN
        val protoDto = GetBestStockSymbolsWithStockRecordsResponse.getDefaultInstance()
        // WHEN THEN
        val exception = assertThrows<RuntimeException> { protoDto.toAggregatedStockItemResponseDto() }
        assertEquals("Required message is empty", exception.message)
    }
}
