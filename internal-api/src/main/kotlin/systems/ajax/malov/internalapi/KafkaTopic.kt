package systems.ajax.malov.internalapi

object KafkaTopic {
    private const val REQUEST_PREFIX = "ajax.systems.stockanalyzer.output.pub"

    object KafkaStockPriceEvents {
        private const val STOCK_PREFIX = "$REQUEST_PREFIX.stock"

        const val STOCK_PRICE = "$STOCK_PREFIX.stock_price"
        const val NOTIFICATION_STOCK_PRICE = "$STOCK_PREFIX.notification_stock_price"
    }
}
