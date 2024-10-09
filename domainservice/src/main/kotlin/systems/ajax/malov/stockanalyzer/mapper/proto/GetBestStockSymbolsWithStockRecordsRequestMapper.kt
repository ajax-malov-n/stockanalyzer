package systems.ajax.malov.stockanalyzer.mapper.proto

import com.google.protobuf.ByteString
import systems.ajax.malov.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponse
import systems.ajax.malov.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigDecimalProto
import systems.ajax.malov.internalapi.commonmodel.stock.big_decimal.proto.BigIntegerProto
import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import java.math.BigDecimal
import java.math.BigInteger

object GetBestStockSymbolsWithStockRecordsRequestMapper {
    fun toGetBestStockSymbolsWithStockRecordsRequest(
        aggregatedData: Map<String, List<MongoStockRecord>>,
    ): GetBestStockSymbolsWithStockRecordsResponse {
        val aggregatedItems = aggregatedData.entries
            .map { (symbol, stocks) -> toAggregatedStockRecordItemResponseDto(symbol, stocks) }
            .fold(GetBestStockSymbolsWithStockRecordsResponse.Success.newBuilder()) { successBuilder, next ->
                successBuilder.addStockSymbols(next)
            }

        return GetBestStockSymbolsWithStockRecordsResponse.newBuilder().setSuccess(aggregatedItems).build()
    }

    private fun toAggregatedStockRecordItemResponseDto(
        stock: String,
        stocks: List<MongoStockRecord>,
    ): AggregatedStockRecordItemResponse {
        return stocks.map { toShortStockRecordResponseDto(it) }
            .fold(AggregatedStockRecordItemResponse.newBuilder()) { build, next ->
                build.addData(next)
            }.setStockSymbol(stock)
            .build()
    }

    private fun toShortStockRecordResponseDto(it: MongoStockRecord): ShortStockRecordResponse? =
        ShortStockRecordResponse.newBuilder()
            .setLowPrice(convertToBDecimal(it.lowPrice))
            .setHighPrice(convertToBDecimal(it.highPrice))
            .setOpenPrice(convertToBDecimal(it.openPrice))
            .setCurrentPrice(
                convertToBDecimal(it.currentPrice)
            )
            .build()

    private fun convertToBInteger(bigInteger: BigInteger): BigIntegerProto {
        return BigIntegerProto.newBuilder().apply {
            val bytes: ByteString = ByteString.copyFrom(bigInteger.toByteArray())
            setValue(bytes)
        }.build()
    }

    private fun convertToBDecimal(bigDecimal: BigDecimal?): BigDecimalProto {
        val scale: Int = bigDecimal?.scale() ?: 0
        return BigDecimalProto.newBuilder()
            .setScale(scale)
            .setIntVal(convertToBInteger(bigDecimal?.unscaledValue() ?: BigInteger.ZERO))
            .build()
    }
}
