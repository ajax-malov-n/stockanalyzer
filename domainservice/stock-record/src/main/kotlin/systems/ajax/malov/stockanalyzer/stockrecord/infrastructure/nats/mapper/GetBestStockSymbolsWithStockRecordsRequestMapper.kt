package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.nats.mapper

import systems.ajax.malov.commonmodel.stock.ShortStockRecord
import systems.ajax.malov.internalapi.input.reqreply.stock.AggregatedStockRecordItemResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockanalyzer.core.shared.mapper.proto.BigDecimalProtoMapper.convertToBigDecimalProto
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord

object GetBestStockSymbolsWithStockRecordsRequestMapper {
    fun toGetBestStockSymbolsWithStockRecordsRequest(
        aggregatedData: Map<String, List<StockRecord>>,
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
        stocks: List<StockRecord>,
    ): AggregatedStockRecordItemResponse {
        return stocks.map { toShortStockRecordResponseDto(it) }
            .fold(AggregatedStockRecordItemResponse.newBuilder()) { build, next ->
                build.addData(next)
            }.setStockSymbol(stock)
            .build()
    }

    private fun toShortStockRecordResponseDto(it: StockRecord): ShortStockRecord =
        ShortStockRecord.newBuilder()
            .setLowPrice(convertToBigDecimalProto(it.lowPrice))
            .setHighPrice(convertToBigDecimalProto(it.highPrice))
            .setOpenPrice(convertToBigDecimalProto(it.openPrice))
            .setCurrentPrice(convertToBigDecimalProto(it.currentPrice))
            .build()
}
