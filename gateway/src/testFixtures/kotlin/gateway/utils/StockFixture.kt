package gateway.utils

import com.google.protobuf.ByteString
import com.google.protobuf.Timestamp
import systems.ajax.malov.commonmodel.stock.BigDecimalProto
import systems.ajax.malov.commonmodel.stock.BigIntegerProto
import systems.ajax.malov.commonmodel.stock.ShortStockRecord
import systems.ajax.malov.commonmodel.stock.StockPrice
import systems.ajax.malov.gateway.infrastructure.dto.GetBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.internalapi.input.reqreply.stock.AggregatedStockRecordItemResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import java.nio.ByteBuffer

object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    fun createGetBestStockSymbolsWithStockRecordsRequestDto(quantity: Int) =
        GetBestStockSymbolsWithStockRecordsRequestDto(quantity)

    fun createStockPrice() = StockPrice.newBuilder().apply {
        stockSymbolName = TEST_STOCK_SYMBOL
        price = BigDecimalProto.newBuilder()
            .apply {
                scale = 2
                intVal = BigIntegerProto.newBuilder()
                    .apply {
                        value = longToByteString(14500)
                    }
                    .build()
            }
            .build()
        timestamp = Timestamp.newBuilder()
            .apply {
                nanos = 111111
                seconds = 11111
            }.build()
    }.build()

    fun createGetBestStockSymbolsWithStockRecordsResponse(): GetBestStockSymbolsWithStockRecordsResponse {
        val stockRecords: MutableList<ShortStockRecord> = ArrayList()

        val openPrice = BigDecimalProto.newBuilder()
            .apply {
                scale = 2
                intVal = BigIntegerProto.newBuilder()
                    .apply {
                        value = longToByteString(14500)
                    }
                    .build()
            }
            .build()

        val highPrice = BigDecimalProto.newBuilder()
            .apply {
                scale = 2
                intVal = BigIntegerProto.newBuilder()
                    .apply {
                        value = longToByteString(14500)
                    }
                    .build()
            }
            .build()

        val lowPrice = BigDecimalProto.newBuilder()
            .apply {
                scale = 2
                intVal = BigIntegerProto.newBuilder()
                    .apply {
                        value = longToByteString(14500)
                    }
                    .build()
            }
            .build()

        val currentPrice = BigDecimalProto.newBuilder()
            .apply {
                scale = 2
                intVal = BigIntegerProto.newBuilder()
                    .apply {
                        value = longToByteString(14500)
                    }
                    .build()
            }
            .build()

        val stockRecord: ShortStockRecord = createShortStockRecord(
            openPrice,
            highPrice,
            lowPrice,
            currentPrice
        )


        repeat(5) {
            stockRecords.add(stockRecord)
        }

        val aggregatedStockRecord: AggregatedStockRecordItemResponse =
            AggregatedStockRecordItemResponse.newBuilder().apply {
                stockSymbol = TEST_STOCK_SYMBOL
                addAllData(stockRecords)
            }.build()
        return GetBestStockSymbolsWithStockRecordsResponse.newBuilder()
            .apply {
                successBuilder.addStockSymbols(aggregatedStockRecord)
            }
            .build()
    }

    private fun longToByteString(value: Long): ByteString {
        val byteArray = ByteBuffer.allocate(8).putLong(value).array()
        return ByteString.copyFrom(byteArray)
    }

    private fun createShortStockRecord(
        openPrice: BigDecimalProto, highPrice: BigDecimalProto,
        lowPrice: BigDecimalProto, currentPrice: BigDecimalProto,
    ): ShortStockRecord {
        return ShortStockRecord.newBuilder().also {
            it.openPrice = openPrice
            it.highPrice = highPrice
            it.lowPrice = lowPrice
            it.currentPrice = currentPrice
        }.build()
    }
}

