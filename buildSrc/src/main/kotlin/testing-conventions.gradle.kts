import gradle.kotlin.dsl.accessors._8f24d8570b6648d28693022272e4c30c.testImplementation
import gradle.kotlin.dsl.accessors._8f24d8570b6648d28693022272e4c30c.testRuntimeOnly
import systems.ajax.malov.stockanalyzer.libDeps

plugins {
    id("kotlin-conventions")
    jacoco
    `java-test-fixtures`
    `jvm-test-suite`
}


testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation(libDeps.pReactoTest)
                implementation("org.jetbrains.kotlin:kotlin-test-junit5")
                implementation("org.junit.platform:junit-platform-launcher")
            }
        }
    }
}

dependencies {
    testImplementation(libDeps.archunit)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libDeps.spring.mockk)
    testImplementation(libDeps.pReactoTest)
}

configurations {
    named("testFixturesImplementation") {
        extendsFrom(configurations.implementation.get())
    }
}

