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

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
