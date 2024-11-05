package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.nats.mapper

import com.google.protobuf.Timestamp
import stockanalyzer.stockrecord.utils.StockFixture.domainStockRecord
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.kafka.mapper.StockRecordMapper.toStockPrice
import kotlin.test.Test
import kotlin.test.assertEquals

class StockPriceMapperTest {

    @Test
    fun `should convert MongoStockRecord when dateOfRetrieval is null to StockPrice`() {
        // Given
        val mongoRecord: StockRecord = domainStockRecord()

        // When
        val actual = mongoRecord.toStockPrice()

        // Then
        val expected = StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(mongoRecord.currentPrice))
            .setStockSymbolName(mongoRecord.symbol)
            .setTimestamp(
                Timestamp.newBuilder()
                    .setSeconds(mongoRecord.dateOfRetrieval?.epochSecond ?: 0)
                    .setNanos(mongoRecord.dateOfRetrieval?.nano ?: 0)
                    .build()
            )
            .build()
        assertEquals(expected, actual)
    }

    @Test
    fun `should convert MongoStockRecord to StockPrice`() {
        // Given
        val mongoRecord: StockRecord = domainStockRecord()
            .copy(dateOfRetrieval = null)

        // When
        val actual = mongoRecord.toStockPrice()

        // Then
        val expected = StockPrice.newBuilder()
            .setPrice(convertToBigDecimalProto(mongoRecord.currentPrice))
            .setStockSymbolName(mongoRecord.symbol)
            .build()
        assertEquals(expected, actual)
    }
}
