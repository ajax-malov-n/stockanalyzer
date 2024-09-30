package systems.ajax.malov.stockanalyzer.controller.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val stockRecordAnalyzerService: StockRecordAnalyzerService) {

    @GetMapping("/bestFive")
    fun getFiveBestStockSymbolsWithStockRecords(): Mono<AggregatedStockRecordResponseDto> =
        stockRecordAnalyzerService
            .getFiveBestStockSymbolsWithStockRecords()
            .map {
                toAggregatedStockItemResponseDto(it)
            }

    @GetMapping("/symbols")
    fun getAllManageableStockSymbols(): Mono<List<String>> =
        stockRecordAnalyzerService.getAllManageableStocksSymbols()
}
