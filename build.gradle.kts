import io.github.surpsg.deltacoverage.gradle.DeltaCoverageConfiguration

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.github.surpsg.delta-coverage") version "2.4.0"
    id("jacoco")
    `java-test-fixtures`
}

group = "systems.ajax.malov"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    implementation("io.finnhub:kotlin-client:2.0.20")
//    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<DeltaCoverageConfiguration> {
    diffSource.git.compareWith("refs/remotes/origin/master")

    violationRules.failIfCoverageLessThan(0.6)
    reports {
        html.set(true)
    }
}

tasks.named("check") {
    dependsOn("deltaCoverage")
}
