import io.gitlab.arturbosch.detekt.Detekt
import systems.ajax.malov.stockanalyzer.libDeps

plugins {
    kotlin("jvm")
    jacoco
    `java-test-fixtures`
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    detektPlugins(libDeps.detekt.formatting)
}


val targetJvmVersion = JavaLanguageVersion.of(21)
kotlin {
    jvmToolchain {
        languageVersion.set(targetJvmVersion)
    }
}

java {
    toolchain {
        languageVersion.set(targetJvmVersion)
    }
}

detekt {
    buildUponDefaultConfig = true
    config.from(file("${rootDir}/detekt.yml"))
}

tasks.withType<Detekt> {
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

configurations {
    named("testFixturesImplementation") {
        extendsFrom(configurations.implementation.get())
    }
}
