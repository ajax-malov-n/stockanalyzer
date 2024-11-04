plugins {
    `spring-conventions`
    `grpc-conventions`
}

dependencies {
    implementation(libs.finnhub.kotlin.client)

    implementation(libs.shedlock.spring)
    implementation(libs.shedlock.provider.mongo)

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation(libs.kafka)

    implementation(libs.nats)

    implementation(project(":internal-api"))
    implementation(project(":domainservice:migration"))
    implementation(project(":domainservice:stock-record"))
    implementation(project(":domainservice:user-tracked-symbol"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(libs.jacksonJsr310)

    testImplementation(libs.natsTest)
    testImplementation(libs.kafkaTest)
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
