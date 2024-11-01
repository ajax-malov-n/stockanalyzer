package systems.ajax.malov.gateway.infrastructure.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class GetBestStockSymbolsWithStockRecordsRequestDto(
    @field:Max(value = 5, message = "Quantity must be less than 5")
    @field:Min(value = 1, message = "Quantity must be greater than 1")
    val quantity: Int?,
)
