package gateway.utils

import com.google.protobuf.ByteString
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BInteger
import systems.ajax.malov.commonmodel.stock.short_stock.proto.ShortStockRecordResponseDto
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import java.nio.ByteBuffer


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    fun createGetFiveBestStockSymbolsWithStockRecordsResponse(): GetFiveBestStockSymbolsWithStockRecordsResponse {
        val stockRecords: MutableList<ShortStockRecordResponseDto> = ArrayList()

        val openPrice = BDecimal.newBuilder()
            .setScale(2)
            .setIntVal(
                BInteger.newBuilder()
                    .setValue(longToByteString(14500))
                    .build()
            )
            .build()

        val highPrice = BDecimal.newBuilder()
            .setScale(2)
            .setIntVal(
                BInteger.newBuilder()
                    .setValue(longToByteString(15000))
                    .build()
            )
            .build()

        val lowPrice = BDecimal.newBuilder()
            .setScale(2)
            .setIntVal(
                BInteger.newBuilder()
                    .setValue(longToByteString(14000))
                    .build()
            )
            .build()

        val currentPrice = BDecimal.newBuilder()
            .setScale(2)
            .setIntVal(
                BInteger.newBuilder()
                    .setValue(longToByteString(14550))
                    .build()
            )
            .build()

        val stockRecord: ShortStockRecordResponseDto = createShortStockRecord(
            openPrice,
            highPrice,
            lowPrice,
            currentPrice
        )


        stockRecords.add(stockRecord)

        val aggregatedStockRecord: AggregatedStockRecordItemResponseDto =
            AggregatedStockRecordItemResponseDto.newBuilder()
                .setStockSymbol(TEST_STOCK_SYMBOL)
                .addAllData(stockRecords)
                .build()
        return GetFiveBestStockSymbolsWithStockRecordsResponse.newBuilder()
            .addStockSymbols(aggregatedStockRecord).build()
    }

    private fun longToByteString(value: Long): ByteString {
        val byteArray = ByteBuffer.allocate(8).putLong(value).array()
        return ByteString.copyFrom(byteArray)
    }

    private fun createShortStockRecord(
        openPrice: BDecimal, highPrice: BDecimal,
        lowPrice: BDecimal, currentPrice: BDecimal,
    ): ShortStockRecordResponseDto {
        return ShortStockRecordResponseDto.newBuilder()
            .setOpenPrice(openPrice)
            .setHighPrice(highPrice)
            .setLowPrice(lowPrice)
            .setCurrentPrice(currentPrice)
            .build()
    }
}

