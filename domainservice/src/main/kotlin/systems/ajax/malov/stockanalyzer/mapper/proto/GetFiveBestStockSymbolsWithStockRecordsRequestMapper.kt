package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.ByteString
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponse
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BInteger
import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.math.BigDecimal
import java.math.BigInteger

object GetFiveBestStockSymbolsWithStockRecordsRequestMapper {
    fun toGetFiveBestStockSymbolsWithStockRecordsRequest(aggregatedData: Map<String, List<MongoStockRecord>>):
        GetNBestStockSymbolsWithStockRecordsResponse {
        return GetNBestStockSymbolsWithStockRecordsResponse.newBuilder().apply {
            successBuilder.addAllStockSymbols(
                aggregatedData.map { (symbol, stocks) ->
                    toAggregatedStockRecordItemResponseDto(symbol, stocks)
                }
            )
        }.build()
    }

    private fun toAggregatedStockRecordItemResponseDto(
        stock: String,
        stocks: List<MongoStockRecord>,
    ): AggregatedStockRecordItemResponse? =
        AggregatedStockRecordItemResponse.newBuilder().setStockSymbol(stock)
            .addAllData(
                stocks.map {
                    toShortStockRecordResponseDto(it)
                }
            ).build()

    private fun toShortStockRecordResponseDto(it: MongoStockRecord): ShortStockRecordResponse? =
        ShortStockRecordResponse.newBuilder()
            .setLowPrice(convertToBDecimal(it.lowPrice))
            .setHighPrice(convertToBDecimal(it.highPrice))
            .setOpenPrice(convertToBDecimal(it.openPrice))
            .setCurrentPrice(
                convertToBDecimal(it.currentPrice)
            )
            .build()

    private fun convertToBInteger(bigInteger: BigInteger): BInteger {
        val builder = BInteger.newBuilder()
        val bytes: ByteString = ByteString.copyFrom(bigInteger.toByteArray())
        builder.setValue(bytes)
        return builder.build()
    }

    private fun convertToBDecimal(bigDecimal: BigDecimal?): BDecimal {
        val scale: Int = bigDecimal?.scale() ?: 0
        return BDecimal.newBuilder()
            .setScale(scale)
            .setIntVal(convertToBInteger(bigDecimal?.unscaledValue() ?: BigInteger.ZERO))
            .build()
    }
}
