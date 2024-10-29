package systems.ajax.malov.stockanalyzer.repository.impl

import io.lettuce.core.RedisException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.ReadOnlyStockRecordRepository
import systems.ajax.malov.stockanalyzer.repository.StockRecordRepository
import java.net.SocketException
import java.time.Duration
import java.util.Date

@Repository
class RedisStockRecordRepository(
    private val reactiveStringRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val reactiveMapRedisTemplate: ReactiveRedisTemplate<String, Map<String, List<MongoStockRecord>>>,
    @Value("\${spring.data.redis.key.prefix}")
    private val stockRecordPrefix: String,
    @Value("\${spring.data.redis.ttl.minutes}")
    private val redisTtlMinutes: Long,
    private val stockMongoRecordRepository: StockRecordRepository,
) : ReadOnlyStockRecordRepository {
    override fun findTopStockSymbolsWithStockRecords(
        quantity: Int,
        from: Date,
        to: Date,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        return reactiveMapRedisTemplate.opsForValue()
            .get(createTopStockSymbolsWithStockRecordsKey(quantity, stockRecordPrefix))
            .log()
            .onErrorResume(::isRedisOrSocketException) {
                log.error(it.message, it)
                stockMongoRecordRepository.findTopStockSymbolsWithStockRecords(quantity, from, to)
            }
            .switchIfEmpty(
                stockMongoRecordRepository.findTopStockSymbolsWithStockRecords(quantity, from, to).flatMap {
                    saveTopStockSymbolsWithStockRecords(quantity, it)
                }
            ).log()
    }

    override fun findAllStockSymbols(): Flux<String> {
        return reactiveStringRedisTemplate.opsForList()
            .range(createAllStockSymbolsKey(stockRecordPrefix), 0, -1)
            .onErrorResume(::isRedisOrSocketException) {
                log.error(it.message, it)
                stockMongoRecordRepository.findAllStockSymbols()
            }
            .switchIfEmpty(
                stockMongoRecordRepository.findAllStockSymbols()
                    .collectList()
                    .flatMapMany {
                        saveAllStockSymbols(it)
                    }
            )
    }

    private fun saveTopStockSymbolsWithStockRecords(
        quantity: Int,
        topStocksMap: Map<String, List<MongoStockRecord>>,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        return reactiveMapRedisTemplate.opsForValue()
            .set(
                createTopStockSymbolsWithStockRecordsKey(quantity, stockRecordPrefix),
                topStocksMap,
                Duration.ofMinutes(redisTtlMinutes)
            ).then(topStocksMap.toMono())
    }

    private fun saveAllStockSymbols(stockSymbols: MutableList<String>): Flux<String> {
        val script =
            "local ttlInSeconds = $redisTtlMinutes * 60; " +
                "redis.call('LPUSH', KEYS[1], unpack(ARGV)); " +
                "redis.call('EXPIRE', KEYS[1], ttlInSeconds); "

        return reactiveStringRedisTemplate
            .execute<String>(RedisScript.of(script), listOf(createAllStockSymbolsKey(stockRecordPrefix)), stockSymbols)
            .thenMany(stockSymbols.toFlux())
    }

    private fun isRedisOrSocketException(error: Throwable): Boolean {
        return listOf(
            RedisConnectionFailureException::class.java,
            RedisException::class.java,
            SocketException::class.java
        ).any { it.isInstance(error) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(RedisStockRecordRepository::class.java)

        fun createTopStockSymbolsWithStockRecordsKey(quantity: Int, prefix: String): String {
            return "$prefix${RedisStockRecordRepository::findTopStockSymbolsWithStockRecords.name}-$quantity"
        }

        fun createAllStockSymbolsKey(prefix: String): String {
            return "$prefix${RedisStockRecordRepository::findAllStockSymbols.name}"
        }
    }
}
