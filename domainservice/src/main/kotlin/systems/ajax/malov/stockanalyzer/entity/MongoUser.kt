package systems.ajax.malov.stockanalyzer.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser(
    @Id
    var id: ObjectId?,
    val email: String?,
    val trackedSymbols: List<MongoUserTrackedSymbol> = emptyList(),
) {
    companion object {
        const val COLLECTION_NAME = "users"
    }
}
