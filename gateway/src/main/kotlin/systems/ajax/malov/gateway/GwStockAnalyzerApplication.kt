package systems.ajax.malov.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GwStockAnalyzerApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<GwStockAnalyzerApplication>(*args)
}
