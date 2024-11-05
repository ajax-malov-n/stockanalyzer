plugins {
    `spring-conventions`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    api("org.springframework.boot:spring-boot-starter-data-mongodb")

    api(libs.kafka)

    implementation(project(":internal-api"))

    api(libs.nats)
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    enabled = false
}
