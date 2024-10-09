plugins {
    `kotlin-conventions`
    id("delta-coverage-conventions")
}

allprojects {
    group = "systems.ajax.malov"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

tasks.named("check") {
    dependsOn("deltaCoverage")
    dependsOn("detektMain")
    dependsOn("detektTest")
}
