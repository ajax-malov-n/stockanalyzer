package systems.ajax.malov.internalapi

object NatsSubject {
    private const val REQUEST_PREFIX = "ajax.systems.stockanalyzer.input.request"

    object StockRequest {
        private const val STOCK_PREFIX = "$REQUEST_PREFIX.stock"

        const val GET_FIVE_BEST_STOCK_SYMBOLS = "$STOCK_PREFIX.get_five_best_stock_symbols"
        const val GET_ALL_MAN_SYMBOLS = "$STOCK_PREFIX.get_all_man_symbols"
    }
}