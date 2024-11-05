package systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo

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
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.application.port.output.UserTrackedSymbolRepositoryOutPort
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.domain.UserTrackedSymbol
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.entity.MongoUserTrackedSymbol
import systems.ajax.malov.stockanalyzer.usertrackedsymbol.infrastructure.mongo.mapper.UserTrackedSymbolMapper.toDomain
import java.math.BigDecimal

@Repository
class MongoUserTrackedSymbolRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : UserTrackedSymbolRepositoryOutPort {

    override fun findUserIdsToNotify(
        stockSymbolName: String,
        currentPrice: BigDecimal,
    ): Flux<UserTrackedSymbol> {
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
            ).map {
                it.toDomain()
            }
    }

    override fun deleteUserTrackedSymbol(ids: List<String>): Mono<Unit> {
        return Mono.defer {
            if (ids.isEmpty()) {
                Mono.just(Unit)
            } else {
                reactiveMongoTemplate.findAllAndRemove<UserTrackedSymbol>(
                    Query.query(
                        Criteria.where(Fields.UNDERSCORE_ID).`in`(
                            ids.map { ObjectId(it) }
                        )
                    ),
                    MongoUserTrackedSymbol.COLLECTION_NAME
                ).then(Mono.just(Unit))
            }
        }
    }
}
