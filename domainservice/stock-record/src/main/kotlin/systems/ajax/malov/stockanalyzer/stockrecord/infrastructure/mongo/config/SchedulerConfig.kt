package systems.ajax.malov.stockanalyzer.stockrecord.infrastructure.mongo.config

import com.mongodb.client.MongoClient
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = ["scheduling.enabled"], havingValue = "true", matchIfMissing = true)
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
class SchedulerConfig {

    @Bean
    fun lockProvider(mongoClient: MongoClient): LockProvider {
        return MongoLockProvider(mongoClient.getDatabase("stocks"))
    }
}
