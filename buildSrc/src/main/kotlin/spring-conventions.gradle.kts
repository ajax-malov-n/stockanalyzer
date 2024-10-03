import gradle.kotlin.dsl.accessors._9f7f456724d17774e707e28dbf6a22c7.implementation
import systems.ajax.malov.stockanalyzer.libDeps

plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}


dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libDeps.spring.mockk)

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libDeps.pReactoTest)
}
