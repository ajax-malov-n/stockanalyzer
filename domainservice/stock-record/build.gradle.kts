plugins {
    `subproject-spring-conventions`
    `grpc-conventions`
    `testing-conventions`
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

    integrationTestImplementation(project(":domainservice:core"))
    integrationTestImplementation(testFixtures(project(":domainservice:stock-record")))
    integrationTestImplementation(libs.natsTest)
    integrationTestImplementation(libs.kafkaTest)
    integrationTestImplementation(project(":internal-api"))
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}

