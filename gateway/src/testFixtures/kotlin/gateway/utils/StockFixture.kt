package gateway.utils

import com.google.protobuf.ByteString
import systems.ajax.malov.gateway.dto.GetNBestStockSymbolsWithStockRecordsRequestDto
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponse
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BInteger
import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse
import java.nio.ByteBuffer


object StockFixture {
    const val TEST_STOCK_SYMBOL = "AAPL"
    fun createGetNBestStockSymbolsWithStockRecordsRequestDto(n: Int) = GetNBestStockSymbolsWithStockRecordsRequestDto(n)
    fun createGetNBestStockSymbolsWithStockRecordsResponse(): GetNBestStockSymbolsWithStockRecordsResponse {
        val stockRecords: MutableList<ShortStockRecordResponse> = ArrayList()

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
        return GetNBestStockSymbolsWithStockRecordsResponse.newBuilder()
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
        openPrice: BDecimal, highPrice: BDecimal,
        lowPrice: BDecimal, currentPrice: BDecimal,
    ): ShortStockRecordResponse {
        return ShortStockRecordResponse.newBuilder()
            .setOpenPrice(openPrice)
            .setHighPrice(highPrice)
            .setLowPrice(lowPrice)
            .setCurrentPrice(currentPrice)
            .build()
    }
}

