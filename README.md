# Stockanalyzer

An application which manages and aggregates stock data

# Current Technologies

Docker, Gradle

## Description

Stockanalyzer uses external API to retrieve stock data and persists it to an in-memory database.
The aggregation process happens every 1 minute.

External API [Finnhub](https://finnhub.io/docs/api/quote)

There are two feature now:

+ Retrieve all manageable stocks symbols from db
+ Find five best stocks to based on current database data

Entities:

```kotlin 
  data class Stock(
    // A unique identifier for the stock, typically generated automatically. 
    // It helps distinguish between different stock entries in a system.
    var id: UUID?,

    // The stock ticker symbol, usually a short, unique series of letters 
    // representing the publicly traded company on an exchange (e.g., "AAPL" for Apple Inc.).
    val symbol: String?,

    // The price at which the stock opened for trading on a particular day.
    // This value is determined by the first trade executed after the market opens.
    val openPrice: Float?,

    // The highest price at which the stock traded during the trading day.
    // It gives an indication of the maximum price traders were willing to pay.
    val highPrice: Float?,

    // The lowest price at which the stock traded during the trading day.
    // It reflects the minimum price at which the stock was sold.
    val lowPrice: Float?,

    // The last recorded price of the stock at the time of data retrieval.
    // This value is frequently updated throughout the trading day.
    val currentPrice: Float?,

    // The price at which the stock closed on the previous trading day.
    // It is used as a reference point to gauge price changes.
    val previousClosePrice: Float?,

    // The absolute difference between the current price and the previous close price.
    // It indicates whether the stock has gone up or down in value.
    val change: Float?,

    // The percentage change from the previous close price to the current price.
    // This gives a relative measure of how much the stock has gained or lost in value.
    val percentChange: Float?,

    // The timestamp when the stock data was retrieved.
    // It helps track the exact time when the stock information was obtained.
    val dateOfRetrieval: Instant?
)
```
