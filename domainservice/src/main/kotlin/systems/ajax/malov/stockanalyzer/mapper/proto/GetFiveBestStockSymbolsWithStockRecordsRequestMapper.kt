package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.ByteString
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BInteger
import systems.ajax.malov.commonmodel.stock.short_stock.proto.ShortStockRecordResponseDto
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.math.BigDecimal
import java.math.BigInteger

object GetFiveBestStockSymbolsWithStockRecordsRequestMapper {
    fun toGetFiveBestStockSymbolsWithStockRecordsRequest(aggregatedData: Map<String, List<MongoStockRecord>>):
        GetFiveBestStockSymbolsWithStockRecordsResponse {
        fun convertToBInteger(bigInteger: BigInteger): BInteger {
            val builder = BInteger.newBuilder()
            val bytes: ByteString = ByteString.copyFrom(bigInteger.toByteArray())
            builder.setValue(bytes)
            return builder.build()
        }

        fun convertToBDecimal(bigDecimal: BigDecimal?): BDecimal {
            val scale: Int = bigDecimal?.scale() ?: 0
            return BDecimal.newBuilder()
                .setScale(scale)
                .setIntVal(convertToBInteger(bigDecimal?.unscaledValue() ?: BigInteger.ZERO))
                .build()
        }

        return GetFiveBestStockSymbolsWithStockRecordsResponse.newBuilder()
            .addAllStockSymbols(
                aggregatedData.map { (symbol, stocks) ->
                    symbol.let { stock ->
                        AggregatedStockRecordItemResponseDto.newBuilder().setStockSymbol(stock)
                            .addAllData(
                                stocks.map {
                                    ShortStockRecordResponseDto.newBuilder()
                                        .setLowPrice(convertToBDecimal(it.lowPrice))
                                        .setHighPrice(convertToBDecimal(it.highPrice))
                                        .setOpenPrice(convertToBDecimal(it.openPrice))
                                        .setCurrentPrice(
                                            convertToBDecimal(it.currentPrice)
                                        )
                                        .build()
                                }
                            ).build()
                    }
                }
            )
            .build()
    }
}
