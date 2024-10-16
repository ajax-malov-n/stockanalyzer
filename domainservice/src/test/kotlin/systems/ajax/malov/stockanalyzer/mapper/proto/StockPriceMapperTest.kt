package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.Timestamp
import stockanalyzer.utils.StockFixture.savedStockRecord
import systems.ajax.malov.internalapi.output.pubsub.stock.StockPrice
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.mapper.proto.StockPriceMapper.toStockPrice
import kotlin.test.Test
import kotlin.test.assertEquals

class StockPriceMapperTest {

    @Test
    fun `should convert MongoStockRecord when dateOfRetrieval is null to StockPrice`() {
        // Given
        val mongoRecord: MongoStockRecord = savedStockRecord()

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
        val mongoRecord: MongoStockRecord = savedStockRecord()
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
