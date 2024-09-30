package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.ByteString
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BInteger
import systems.ajax.malov.commonmodel.stock.short_stock.proto.ShortStockRecordResponseDto
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.AggregatedStockRecordItemResponseDto
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetFiveBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject.StockRequest.GET_FIVE_BEST_STOCK_SYMBOLS
import systems.ajax.malov.stockanalyzer.controller.nats.NatsController
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService
import java.math.BigDecimal
import java.math.BigInteger


@Component
class GetFiveBestStockSymbolsWithStockRecordsNatsController
    (
    override val connection: Connection,
    private val stockRecordAnalyzerServiceImpl: StockRecordAnalyzerService,
) : NatsController<GetFiveBestStockSymbolsWithStockRecordsRequest, GetFiveBestStockSymbolsWithStockRecordsResponse> {
    override val subject: String = GET_FIVE_BEST_STOCK_SYMBOLS
    override val parser: Parser<GetFiveBestStockSymbolsWithStockRecordsRequest> =
        GetFiveBestStockSymbolsWithStockRecordsRequest.parser()

    override fun handle(request: GetFiveBestStockSymbolsWithStockRecordsRequest): Mono<GetFiveBestStockSymbolsWithStockRecordsResponse> {
        return stockRecordAnalyzerServiceImpl.getFiveBestStockSymbolsWithStockRecords()
            .map { buildResponse(it) }
    }

    private fun buildResponse(aggregatedData: Map<String, List<MongoStockRecord>>): GetFiveBestStockSymbolsWithStockRecordsResponse {

        return GetFiveBestStockSymbolsWithStockRecordsResponse.newBuilder()
            .addAllStockSymbols(aggregatedData.map { (symbol, stocks) ->
                symbol.let { stock ->
                    AggregatedStockRecordItemResponseDto.newBuilder().setStockSymbol(stock)
                        .addAllData(stocks.map {
                            ShortStockRecordResponseDto.newBuilder()
                                .setLowPrice(convertToBDecimal(it.lowPrice))
                                .setHighPrice(convertToBDecimal(it.highPrice))
                                .setOpenPrice(convertToBDecimal(it.openPrice))
                                .setCurrentPrice(convertToBDecimal(it.currentPrice))
                                .build()
                        }).build()
                }
            })
            .build()
    }

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
}