package systems.ajax.malov.stockanalyzer.controller.nats.stock

import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.mockk.InternalPlatformDsl.toArray
import io.nats.client.Connection
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import stockanalyzer.utils.StockFixture
import stockanalyzer.utils.StockFixture.testDate
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BDecimal
import systems.ajax.malov.commonmodel.stock.big_decimal.proto.BInteger
import systems.ajax.malov.commonmodel.stock.short_stock.proto.ShortStockRecordResponseDto
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetFiveBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetFiveBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import systems.ajax.malov.stockanalyzer.repository.impl.MongoStockRecordRepository
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class NatsControllersTest : AbstractMongoIntegrationTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var mongoStockRecordRepository: MongoStockRecordRepository

    @Test
    fun `getAllManageableSymbols should return success response`() {
        // GIVEN
        val listOfUnsavedStocks = listOf(
            StockFixture.unsavedStockRecord()
                .copy(symbol = "AJAXSYS", dateOfRetrieval = testDate.plus(Duration.ofDays(10)))
        )
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .collectList()
            .block()
        val request = GetAllManageableStockSymbolsRequest
            .getDefaultInstance()
        val symbols = mongoStockRecordRepository.findAllStockSymbols()
            .collectList()
            .block()
        val expectedResponse = GetAllManageableStockSymbolsResponse.newBuilder().apply {
            addAllSymbols(symbols)
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
            request,
            GetAllManageableStockSymbolsResponse.parser()
        )

        // THEN
        assertEquals(hashSetOf(expectedResponse),hashSetOf(actual))
    }


    @Test
    fun `getFiveBestStocksToBuy should return success response`() {
        // GIVEN
        val bestStock1 = StockFixture.firstPlaceStockRecord()
        val bestStock2 = StockFixture.alsoFirstPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .collectList()
            .block()
        val from = Date.from(testDate().minus(1, ChronoUnit.HOURS))
        val to = Date.from(testDate())

        val expectedResponse =
            mongoStockRecordRepository.findTopNStockSymbolsWithStockRecords(5, from, to)
            .map {
                buildResponse(it)
            }
           .block()
        val request = GetFiveBestStockSymbolsWithStockRecordsRequest
            .getDefaultInstance()

        // WHEN
        val actual = doRequest(
            NatsSubject.StockRequest.GET_FIVE_BEST_STOCK_SYMBOLS,
            request,
            GetFiveBestStockSymbolsWithStockRecordsResponse.parser()
        )

        assertEquals(expectedResponse,actual)
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }

    private fun buildResponse(aggregatedData: Map<String, List<MongoStockRecord>>): GetFiveBestStockSymbolsWithStockRecordsResponse {

        return GetFiveBestStockSymbolsWithStockRecordsResponse.newBuilder()
            .addAllStockSymbols(aggregatedData.map { (symbol, stocks) ->
                symbol.let { stock ->
                    systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.AggregatedStockRecordItemResponseDto.newBuilder().setStockSymbol(stock)
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