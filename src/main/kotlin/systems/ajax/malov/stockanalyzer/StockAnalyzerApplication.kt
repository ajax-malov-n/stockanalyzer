package systems.ajax.malov.stockanalyzer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockAnalyzerApplication

fun main(args: Array<String>) {
    runApplication<StockAnalyzerApplication>(*args)
}
