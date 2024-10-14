package systems.ajax.malov.stockanalyzer.repository.impl

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.malov.stockanalyzer.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.repository.UserTrackedSymbolRepository
import java.math.BigDecimal

@Repository
class MongoUserTrackedSymbolRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : UserTrackedSymbolRepository {

    override fun findUserIdsToNotify(
        stockSymbolName: String,
        currentPrice: BigDecimal,
    ): Flux<MongoUserTrackedSymbol> {
        val findQuery = Query.query(
            Criteria.where(MongoUserTrackedSymbol::stockSymbolName.name).isEqualTo(stockSymbolName)
                .and(MongoUserTrackedSymbol::thresholdPrice.name).lte(currentPrice)
        )

        findQuery.fields()
            .include(MongoUserTrackedSymbol::userId.name)
        return reactiveMongoTemplate
            .find<MongoUserTrackedSymbol>(
                findQuery,
                MongoUserTrackedSymbol.COLLECTION_NAME
            )
    }

    override fun deleteUserTrackedSymbol(ids: List<ObjectId>): Mono<Unit> {
        return Mono.defer {
            if (ids.isEmpty()) {
                Mono.just(Unit)
            } else {
                reactiveMongoTemplate.findAllAndRemove<MongoUserTrackedSymbol>(
                    Query.query(
                        Criteria.where(Fields.UNDERSCORE_ID).`in`(ids)
                    ),
                    MongoUserTrackedSymbol.COLLECTION_NAME
                ).then(Mono.just(Unit))
            }
        }
    }
}
