package systems.ajax.malov.gateway.mapper

import systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.gateway.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponse
import systems.ajax.malov.input.reqreply.stock.get_n_best_stock_symbols_with_stocks.proto.GetNBestStockSymbolsWithStockRecordsResponse

object AggregatedStockRecordResponseDtoMapper {

    fun GetNBestStockSymbolsWithStockRecordsResponse.toAggregatedStockItemResponseDto():
        AggregatedStockRecordResponseDto = AggregatedStockRecordResponseDto(
        stockSymbols = success.stockSymbolsList.map { it.toAggregatedStockRecordItemResponse() }
    )
}
