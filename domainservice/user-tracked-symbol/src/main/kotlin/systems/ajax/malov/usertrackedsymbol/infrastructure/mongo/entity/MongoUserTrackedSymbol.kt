package systems.ajax.malov.usertrackedsymbol.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128
import java.math.BigDecimal

@TypeAlias("MongoUserTrackedSymbol")
@Document(collection = MongoUserTrackedSymbol.COLLECTION_NAME)
data class MongoUserTrackedSymbol(
    @Id
    var id: ObjectId,
    val userId: ObjectId,
    val stockSymbolName: String?,
    @Field(targetType = DECIMAL128)
    val thresholdPrice: BigDecimal?,
) {
    companion object {
        const val COLLECTION_NAME = "userTrackedSymbols"
    }
}
