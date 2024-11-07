package systems.ajax.malov.stockanalyzer.stockrecord.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test

class OnionArchitectureTest {
    @Test
    fun onion_architecture_is_respected() {
        val rule = onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("kafka", "..infrastructure.kafka..")
            .adapter("nats", "..infrastructure.nats..")
            .adapter("redis", "..infrastructure.redis..")
            .adapter("mongo", "..infrastructure.mongo..")
            .adapter("finnhub", "..infrastructure.finnhub..")
        rule.check(importedClasses)
    }

    companion object {
        private lateinit var importedClasses: JavaClasses

        @JvmStatic
        @BeforeAll
        fun setup() {
            importedClasses = ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages("systems.ajax.malov.stockrecord")
        }
    }
}
