package systems.ajax.malov.stockanalyzer.controller.grpc.stock

import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import systems.ajax.malov.internalapi.input.reqreply.stock.GetAllManageableStockSymbolsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.internalapi.input.reqreply.stock.GetBestStockSymbolsWithStockRecordsResponse
import systems.ajax.malov.internalapi.service.StockServiceGrpc.StockServiceBlockingStub
import systems.ajax.malov.stockanalyzer.mapper.proto.GetBestStockSymbolsWithStockRecordsRequestMapper.toGetBestStockSymbolsWithStockRecordsRequest
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService
import systems.ajax.malov.stockanalyzer.util.annotations.MockkKafka
import systems.ajax.malov.stockanalyzer.util.annotations.MockkNats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(
    properties = [
        "grpc.server.inProcessName=test",
        "grpc.server.port=-1",
        "grpc.client.inProcess.address=in-process:test"
    ]
)
@MockkNats
@MockkKafka
@ActiveProfiles("test")
class StockGrpcServiceTest {
    @GrpcClient("inProcess")
    private lateinit var client: StockServiceBlockingStub

    @Autowired
    private lateinit var stockRecordAnalyzerService: StockRecordAnalyzerService

    @Test
    fun `should return all manageable stock symbols`() {
        // GIVEN
        val request = GetAllManageableStockSymbolsRequest.getDefaultInstance()
        val expected = stockRecordAnalyzerService
            .getAllManageableStocksSymbols()
            .collectList()
            .block()
        // WHEN
        val actual = client.getAllManageableStocksSymbols(request)
        // THEN
        assertTrue(
            expected!!.containsAll(actual.success.symbolsList),
            "Must have the same content"
        )
    }

    @Test
    fun `should return best stocks symbols with stock data`() {
        // GIVEN
        val request = GetBestStockSymbolsWithStockRecordsRequest.getDefaultInstance()
        val expectedResponse = stockRecordAnalyzerService.getBestStockSymbolsWithStockRecords(5)
            .map {
                toGetBestStockSymbolsWithStockRecordsRequest(it)
            }
            .block()
        // WHEN
        val actual = client.getBestStockSymbolsWithStockRecords(request)
        // THEN
        assertEquals(expectedResponse, actual)
    }

    @Test
    fun `should return failure if quantity is wrong`() {
        // GIVEN
        val request = GetBestStockSymbolsWithStockRecordsRequest.newBuilder().apply {
            quantity = -5
        }.build()
        val expectedResponse = GetBestStockSymbolsWithStockRecordsResponse
            .newBuilder()
            .apply {
                failureBuilder.message = "Quantity must be between 1 and 5"
            }
            .build()
        // WHEN
        val actual = client.getBestStockSymbolsWithStockRecords(request)
        // THEN
        assertTrue(actual.hasFailure(), "Must have failure in a response")
        assertEquals(expectedResponse, actual)
    }

    @Test
    fun `should return three best stocks symbols with stock data`() {
        // GIVEN
        val request = GetBestStockSymbolsWithStockRecordsRequest.newBuilder()
            .apply {
                quantity = 3
            }.build()
        val expectedResponse = stockRecordAnalyzerService.getBestStockSymbolsWithStockRecords(3)
            .map {
                toGetBestStockSymbolsWithStockRecordsRequest(it)
            }
            .block()
        // WHEN
        val actual = client.getBestStockSymbolsWithStockRecords(request)
        // THEN
        assertEquals(expectedResponse, actual)
    }
}
