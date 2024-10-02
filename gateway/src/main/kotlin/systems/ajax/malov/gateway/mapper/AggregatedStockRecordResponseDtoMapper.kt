package systems.ajax.malov.systems.ajax.malov.gateway.mapper


import systems.ajax.malov.input.reqreply.stock.get_five_best_stock_symbols_with_stocks.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.systems.ajax.malov.gateway.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.systems.ajax.malov.gateway.mapper.AggregatedStockRecordItemResponseDtoMapper.toAggregatedStockRecordItemResponse

object AggregatedStockRecordResponseDtoMapper {

    fun GetFiveBestStockSymbolsWithStockRecordsResponse.toAggregatedStockItemResponseDto():
        AggregatedStockRecordResponseDto = AggregatedStockRecordResponseDto(
        stockSymbols = stockSymbolsList.map { it.toAggregatedStockRecordItemResponse() }
    )
}
