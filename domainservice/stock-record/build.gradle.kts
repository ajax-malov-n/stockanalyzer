plugins {
    `spring-conventions`
    `grpc-conventions`
}

dependencies {
    implementation(libs.finnhub.kotlin.client)

    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)

    implementation(libs.shedlock.spring)
    implementation(libs.shedlock.provider.mongo)

    implementation(project(":internal-api"))
    implementation(project(":domainservice:core"))

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
