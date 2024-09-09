import io.github.surpsg.deltacoverage.gradle.DeltaCoverageConfiguration
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.detekt)
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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.mockito.kotlin)
    implementation(libs.finnhub.kotlin.client)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
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

detekt {
    buildUponDefaultConfig = true
    config.from(file("detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

configure<DeltaCoverageConfiguration> {
    val targetBranch = project.properties["diffBase"]?.toString() ?: "refs/remotes/origin/master"
    diffSource.byGit {
        compareWith(targetBranch)
    }

    violationRules.failIfCoverageLessThan(0.6)
    reports {
        html = true
        markdown = true
    }
}

tasks.named("check") {
    dependsOn("deltaCoverage")
    dependsOn("detektMain")
    dependsOn("detektTest")
}
