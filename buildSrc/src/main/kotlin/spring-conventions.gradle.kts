import gradle.kotlin.dsl.accessors._9f7f456724d17774e707e28dbf6a22c7.implementation
import systems.ajax.malov.stockanalyzer.libDeps

plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}


dependencies {
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libDeps.spring.mockk)
    testImplementation(libDeps.pReactoTest)
}
