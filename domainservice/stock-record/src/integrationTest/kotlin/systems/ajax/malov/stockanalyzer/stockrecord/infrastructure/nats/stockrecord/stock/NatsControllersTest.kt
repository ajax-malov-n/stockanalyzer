package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.nats.stockrecord.stock

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import stockanalyzer.stockrecord.utils.StockFixture.alsoFirstPlaceStockRecord
import stockanalyzer.stockrecord.utils.StockFixture.domainStockRecord
import stockanalyzer.stockrecord.utils.StockFixture.firstPlaceStockRecord
import stockanalyzer.stockrecord.utils.StockFixture.testDate
import systems.ajax.malov.internalapi.NatsSubject
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsResponse
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.impl.MongoStockRecordRepository
import systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.nats.mapper.GetBestStockSymbolsWithStockRecordsRequestMapper.toGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.stockrecord.util.IntegrationTestBase
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NatsControllersTest : IntegrationTestBase() {
    @Autowired
    private lateinit var publisher: NatsMessagePublisher

    @Autowired
    private lateinit var mongoStockRecordRepository: MongoStockRecordRepository

    @Test
    fun `getAllManageableSymbols should return success response`() {
        // GIVEN
        val listOfUnsavedStocks = listOf(
            domainStockRecord()
                .copy(symbol = "AJAXSYS", dateOfRetrieval = testDate.plus(Duration.ofDays(10)))
        )
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks).blockLast()
        val request = GetAllManageableStockSymbolsRequest.getDefaultInstance()
        val symbols = mongoStockRecordRepository.findAllStockSymbols().collectList().block()
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
        actual.test()
            .assertNext {
                assertTrue(
                    it.success.symbolsList.containsAll(expectedResponse.success.symbolsList),
                    "Must contain new stock with name AJAXSYS"
                )
            }
            .verifyComplete()
    }

    @Test
    fun `getBestStocksToBuy should return success response`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val bestStock2 = alsoFirstPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1, bestStock2)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks).blockLast()
        val from = Date.from(testDate().minus(1, ChronoUnit.HOURS))
        val to = Date.from(testDate())

        val expectedResponse = mongoStockRecordRepository.findTopStockSymbolsWithStockRecords(5, from, to).map {
            toGetBestStockSymbolsWithStockRecordsRequest(it)
        }.block()!!
        val request = GetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()

        // WHEN
        val actual = doRequest(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            request,
            GetBestStockSymbolsWithStockRecordsResponse.parser()
        )

        // THEN
        actual.test()
            .assertNext {
                assertEquals(
                    it.success.stockSymbolsList.map { record -> record.stockSymbol },
                    expectedResponse.success.stockSymbolsList.map { record -> record.stockSymbol }
                )
            }
            .verifyComplete()
    }

    @Test
    fun `getBestStocksToBuy should return success response with custom quantity`() {
        // GIVEN
        val bestStock1 = firstPlaceStockRecord()
        val listOfUnsavedStocks = listOf(bestStock1)
        mongoStockRecordRepository.insertAll(listOfUnsavedStocks).blockLast()
        val from = Date.from(testDate().minus(1, ChronoUnit.HOURS))
        val to = Date.from(testDate())

        val expectedResponse = mongoStockRecordRepository.findTopStockSymbolsWithStockRecords(1, from, to).map {
            toGetBestStockSymbolsWithStockRecordsRequest(it)
        }.block()!!
        val request = GetBestStockSymbolsWithStockRecordsRequest
            .newBuilder().apply {
                quantity = 1
            }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.StockRequest.GET_N_BEST_STOCK_SYMBOLS,
            request,
            GetBestStockSymbolsWithStockRecordsResponse.parser()
        )

        // THEN
        actual.test()
            .assertNext {
                assertEquals(
                    it.success.stockSymbolsList.map { record -> record.stockSymbol },
                    expectedResponse.success.stockSymbolsList.map { record -> record.stockSymbol }
                )
            }
            .verifyComplete()
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): Mono<ResponseT> {
        return publisher.request(subject, payload, parser)
    }
}
