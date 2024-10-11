package systems.ajax.malov.stockanalyzer.mapper.proto

import systems.ajax.malov.internalapi.commonmodel.stock.short_stock.proto.ShortStockRecordResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.AggregatedStockRecordItemResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto

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
            .setLowPrice(convertToBigDecimalProto(it.lowPrice))
            .setHighPrice(convertToBigDecimalProto(it.highPrice))
            .setOpenPrice(convertToBigDecimalProto(it.openPrice))
            .setCurrentPrice(convertToBigDecimalProto(it.currentPrice))
            .build()
}
