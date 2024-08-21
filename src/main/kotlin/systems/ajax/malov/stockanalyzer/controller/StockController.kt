package systems.ajax.malov.stockanalyzer.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.malov.stockanalyzer.mapper.AggregatedStockResponseDtoMapper.toAggregatedStockItemResponseDto
import systems.ajax.malov.stockanalyzer.service.StockAnalyzerService

@RestController
@RequestMapping("/api/v1/stocks")
class StockController(private val stockAnalyzerService: StockAnalyzerService) {

    @GetMapping("/bestFive")
    fun getFiveBestStocksToBuy() = ResponseEntity
        .ok(
            toAggregatedStockItemResponseDto(
                stockAnalyzerService
                    .getFiveBestStocksToBuy()
            )
        )

    @GetMapping
    fun getAllManageableStockSymbols() = ResponseEntity.ok(stockAnalyzerService.getAllManageableStocksSymbols())
}
