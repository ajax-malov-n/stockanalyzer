package systems.ajax.malov.gateway.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class GetNBestStockSymbolsWithStockRecordsRequestDto(
    @field:Max(value = 5, message = "N must be less than 5")
    @field:Min(value = 1, message = "N must be greater than 1")
    val n: Int?,
)
