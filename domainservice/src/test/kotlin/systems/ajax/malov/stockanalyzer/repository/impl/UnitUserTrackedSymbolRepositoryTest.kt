package systems.ajax.malov.stockanalyzer.repository.impl

import io.mockk.Called
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.kotlin.test.test
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UnitUserTrackedSymbolRepositoryTest {
    @InjectMockKs
    private lateinit var userTrackedSymbolRepository: MongoUserTrackedSymbolRepository

    @MockK
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun `should return unit immediately if list is empty`() {
        // GIVEN
        val emptyList = listOf<ObjectId>()

        // WHEN
        val actual = userTrackedSymbolRepository.deleteUserTrackedSymbol(emptyList)

        // THEN
        actual.test()
            .expectNext(Unit)
            .verifyComplete()

        verify { reactiveMongoTemplate wasNot Called }
    }
}
