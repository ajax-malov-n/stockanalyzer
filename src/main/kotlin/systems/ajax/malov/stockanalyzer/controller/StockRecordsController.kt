package systems.ajax.malov.stockanalyzer.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.malov.stockanalyzer.dto.AggregatedStockRecordResponseDto
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockRecordResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.service.StockRecordAnalyzerService

@RestController
@RequestMapping("/api/v1/stock-records")
class StockRecordsController(private val stockRecordAnalyzerService: StockRecordAnalyzerService) {

    @GetMapping("/bestFive")
    fun getFiveBestStockSymbolsWithStockRecords(): ResponseEntity<AggregatedStockRecordResponseDto> = ResponseEntity
        .ok(
            toAggregatedStockItemResponseDto(
                stockRecordAnalyzerService
                    .getFiveBestStockSymbolsWithStockRecords()
            )
        )

    @GetMapping("/symbols")
    fun getAllManageableStockSymbols(): ResponseEntity<Set<String>> =
        ResponseEntity.ok(stockRecordAnalyzerService.getAllManageableStocksSymbols())
}
