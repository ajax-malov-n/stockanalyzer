package systems.ajax.malov.stockanalyzer.config

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FinnhubApiConfig {
    @Value("\${api.finnhub.token}")
    private lateinit var apiToken: String

    @Bean
    fun finnhubApiConfiguration(): DefaultApi {
        ApiClient.apiKey["token"] = apiToken
        return DefaultApi()
    }
}
