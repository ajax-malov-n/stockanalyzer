package gateway.utils

import com.google.protobuf.ByteString
import systems.ajax.malov.gateway.dto.GetBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponse
import systems.ajax.malov.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigDecimalProto
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigIntegerProto
import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse
import java.nio.ByteBuffer


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    fun createGetBestStockSymbolsWithStockRecordsRequestDto(quantity: Int) =
        GetBestStockSymbolsWithStockRecordsRequestDto(quantity)

    fun createGetBestStockSymbolsWithStockRecordsResponse(): GetBestStockSymbolsWithStockRecordsResponse {
        val stockRecords: MutableList<ShortStockRecordResponse> = ArrayList()

        val openPrice = BigDecimalProto.newBuilder()
            .setScale(2)
            .setIntVal(
                BigIntegerProto.newBuilder()
                    .setValue(longToByteString(14500))
                    .build()
            )
            .build()

        val highPrice = BigDecimalProto.newBuilder()
            .setScale(2)
            .setIntVal(
                BigIntegerProto.newBuilder()
                    .setValue(longToByteString(15000))
                    .build()
            )
            .build()

        val lowPrice = BigDecimalProto.newBuilder()
            .setScale(2)
            .setIntVal(
                BigIntegerProto.newBuilder()
                    .setValue(longToByteString(14000))
                    .build()
            )
            .build()

        val currentPrice = BigDecimalProto.newBuilder()
            .setScale(2)
            .setIntVal(
                BigIntegerProto.newBuilder()
                    .setValue(longToByteString(14550))
                    .build()
            )
            .build()

        val stockRecord: ShortStockRecordResponse = createShortStockRecord(
            openPrice,
            highPrice,
            lowPrice,
            currentPrice
        )


        stockRecords.add(stockRecord)

        val aggregatedStockRecord: AggregatedStockRecordItemResponse =
            AggregatedStockRecordItemResponse.newBuilder()
                .setStockSymbol(TEST_STOCK_SYMBOL)
                .addAllData(stockRecords)
                .build()
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
    ): ShortStockRecordResponse {
        return ShortStockRecordResponse.newBuilder()
            .setOpenPrice(openPrice)
            .setHighPrice(highPrice)
            .setLowPrice(lowPrice)
            .setCurrentPrice(currentPrice)
            .build()
    }
}

