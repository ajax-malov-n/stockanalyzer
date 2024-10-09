package systems.ajax.malov.stockanalyzer.controller.nats.stock

import berlin.yuna.natsserver.embedded.annotation.EnableNatsServer
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired
import stockanalyzer.utils.StockFixture
import stockanalyzer.utils.StockFixture.testDate
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.input.reqreply.stock.get_all_man_sym.proto.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.input.reqreply.stock.get_best_stock_symbols_with_stocks.proto.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.stockanalyzer.mapper.proto.GetBestStockSymbolsWithStockRecordsRequestMapper.toGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.repository.AbstractMongoIntegrationTest
import systems.ajax.malov.stockanalyzer.repository.impl.MongoStockRecordRepository
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@EnableNatsServer(port = 4222)
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
            .blockLast()
        val request = GetAllManageableStockSymbolsRequest
            .getDefaultInstance()
        val symbols = mongoStockRecordRepository.findAllStockSymbols()
            .collectList()
            .block()
        val expectedResponse = GetAllManageableStockSymbolsResponse.newBuilder().apply {
            successBuilder.addAllSymbols(symbols)
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.StockRequest.GET_ALL_MAN_SYMBOLS,
            request,
            GetAllManageableStockSymbolsResponse.parser()
        )

        // THEN
        assertTrue(
            actual.success.symbolsList.containsAll(expectedResponse.success.symbolsList),
            "Must contain new stock with name AJAXSYS"
        )
    }

    @Test
    fun `getBestStocksToBuy should return success response`() {
        // GIVEN
        val bestStock1 = StockFixture.firstPlaceStockRecord()
        val bestStock2 = StockFixture.alsoFirstPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks)
            .blockLast()
        val from = Date.from(testDate().minus(1, ChronoUnit.HOURS))
        val to = Date.from(testDate())

        val expectedResponse =
            mongoStockRecordRepository.findTopStockSymbolsWithStockRecords(5, from, to)
                .map {
                    toGetBestStockSymbolsWithStockRecordsRequest(it)
                }
                .block()
        val request = GetBestStockSymbolsWithStockRecordsRequest
            .getDefaultInstance()

        // WHEN
        val actual = doRequest(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            request,
            GetBestStockSymbolsWithStockRecordsResponse.parser()
        )

        // THEN
        assertEquals(expectedResponse, actual)
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
}
