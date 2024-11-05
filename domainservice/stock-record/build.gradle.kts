plugins {
    `spring-conventions`
    `grpc-conventions`
}

dependencies {
    implementation(libs.finnhub.kotlin.client)

    implementation(libs.shedlock.spring)
    implementation(libs.shedlock.provider.mongo)

    implementation(project(":internal-api"))
    implementation(project(":domainservice:core"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(libs.jacksonJsr310)
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
