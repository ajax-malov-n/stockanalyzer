package systems.ajax.malov.internalapi

object NatsSubject {
    private const val REQUEST_PREFIX = "ajax.systems.stockanalyzer.input.request"
    const val STOCK_QUEUE_GROUP = "stockQueueGroup"

    object StockRequest {
        private const val STOCK_PREFIX = "$REQUEST_PREFIX.stock"

        const val GET_N_BEST_STOCK_SYMBOLS = "$STOCK_PREFIX.get_n_best_stock_symbols"
        const val GET_ALL_MAN_SYMBOLS = "$STOCK_PREFIX.get_all_man_symbols"
    }
}
