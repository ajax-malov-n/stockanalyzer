package systems.ajax.malov.stockanalyzer.repository.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord
import systems.ajax.malov.stockanalyzer.repository.CacheStockRecordRepository
import java.time.Duration

// Interface ???
@Repository
class RedisStockRecordRepository(
    @Qualifier("reactiveStringRedisTemplate")
    private val reactiveStringRedisTemplate: ReactiveRedisTemplate<String, String>,
    @Qualifier("reactiveMapRedisTemplate")
    private val reactiveMapRedisTemplate: ReactiveRedisTemplate<String, Map<String, List<MongoStockRecord>>>,
    @Value("\${spring.data.redis.key.prefix}")
    private val stockRecordPrefix: String,
    @Value("\${spring.data.redis.ttl.minutes}")
    private val redisTtlMinutes: String,
) : CacheStockRecordRepository {
    override fun findTopStockSymbolsWithStockRecords(
        quantity: Int,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        return reactiveMapRedisTemplate.opsForValue()
            .get(createTopStockSymbolsWithStockRecordsKey(quantity, stockRecordPrefix))
    }

    override fun findAllStockSymbols(): Flux<String> {
        return reactiveStringRedisTemplate.opsForList()
            .range(createAllStockSymbolsKey(stockRecordPrefix), 0, -1)
    }

    override fun saveTopStockSymbolsWithStockRecords(
        quantity: Int,
        topStocksMap: Mono<Map<String, List<MongoStockRecord>>>,
    ): Mono<Map<String, List<MongoStockRecord>>> {
        return topStocksMap.flatMap {
            reactiveMapRedisTemplate.opsForValue()
                .set(createTopStockSymbolsWithStockRecordsKey(quantity, stockRecordPrefix), it)
        }.flatMap {
            reactiveMapRedisTemplate
                .expire(
                    createTopStockSymbolsWithStockRecordsKey(quantity, stockRecordPrefix),
                    Duration.ofMinutes(redisTtlMinutes.toLong())
                )
        }.then(topStocksMap)
    }

    override fun saveAllStockSymbols(stringFlux: Flux<String>): Flux<String> {
        return stringFlux.flatMap {
            reactiveStringRedisTemplate.opsForList()
                .leftPush(createAllStockSymbolsKey(stockRecordPrefix), it)
        }.then(
            reactiveStringRedisTemplate.expire(
                createAllStockSymbolsKey(stockRecordPrefix),
                Duration.ofMinutes(redisTtlMinutes.toLong())
            )
        ).thenMany(stringFlux)
    }

    companion object {
        fun createTopStockSymbolsWithStockRecordsKey(quantity: Int, prefix: String): String {
            return "$prefix${RedisStockRecordRepository::findTopStockSymbolsWithStockRecords.name}-$quantity"
        }

        fun createAllStockSymbolsKey(prefix: String): String {
            return "$prefix${RedisStockRecordRepository::findAllStockSymbols.name}"
        }
    }
}
