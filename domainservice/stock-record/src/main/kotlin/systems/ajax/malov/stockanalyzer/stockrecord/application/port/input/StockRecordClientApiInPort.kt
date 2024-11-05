package systems.ajax.malov.stockanalyzer.stockrecord.application.port.input

import reactor.core.publisher.Flux
import systems.ajax.malov.stockanalyzer.stockrecord.domain.StockRecord

interface StockRecordClientApiInPort {
    fun getAllStockRecords(): Flux<StockRecord>
}
