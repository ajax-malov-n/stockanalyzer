# Stockanalyzer
An application which manages and aggregates stock data

## Description

Stockanalyzer uses external API to retrieve stock data and persists it to an in-memory database.
The aggregation process happens every 1 minute.

There are two feature now:
+ Retrieve all manageable stocks symbols from db
+ Find five best stocks to based on current database data

Entities:
```kotlin 
  data class Stock (var id: UUID? = null, val symbol: String?, 
                    val openPrice: Float?, val highPrice: Float?, 
                    val lowPrice: Float?, val currentPrice: Float?, 
                    val previousClosePrice: Float?, val change: Float?, 
                    val percentChange: Float?, val dateOfRetrieval: Instant?
)
```