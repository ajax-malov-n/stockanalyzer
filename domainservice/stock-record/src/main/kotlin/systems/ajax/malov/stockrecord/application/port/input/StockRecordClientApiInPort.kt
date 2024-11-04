package systems.ajax.malov.stockrecord.application.port.input

import reactor.core.publisher.Flux
import systems.ajax.malov.stockrecord.domain.StockRecord

interface StockRecordClientApiInPort {
    fun getAllStockRecords(): Flux<StockRecord>
}
