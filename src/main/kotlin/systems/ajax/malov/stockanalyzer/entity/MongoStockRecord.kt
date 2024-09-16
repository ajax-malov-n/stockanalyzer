package systems.ajax.malov.stockanalyzer.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128
import systems.ajax.malov.stockanalyzer.entity.MongoStockRecord.Companion.COLLECTION_NAME
import java.math.BigDecimal
import java.time.Instant

@TypeAlias("StockRecord")
@Document(collection = COLLECTION_NAME)
data class MongoStockRecord(
    @Id
    var id: ObjectId?,
    val symbol: String?,
    @Field(targetType = DECIMAL128)
    val openPrice: BigDecimal?,
    @Field(targetType = DECIMAL128)
    val highPrice: BigDecimal?,
    @Field(targetType = DECIMAL128)
    val lowPrice: BigDecimal?,
    @Field(targetType = DECIMAL128)
    val currentPrice: BigDecimal?,
    @Field(targetType = DECIMAL128)
    val previousClosePrice: BigDecimal?,
    @Field(targetType = DECIMAL128)
    val change: BigDecimal?,
    @Field(targetType = DECIMAL128)
    val percentChange: BigDecimal?,
    val dateOfRetrieval: Instant?,
) {
    companion object {
        const val COLLECTION_NAME = "stockRecords"
    }
}
