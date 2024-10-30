plugins {
    `spring-conventions`
}

dependencies {
    implementation(libs.finnhub.kotlin.client)

    implementation(libs.jNats)

    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)

    implementation(libs.shedlock.spring)
    implementation(libs.shedlock.provider.mongo)

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("org.springframework.kafka:spring-kafka")
    implementation(libs.reactorKafka)
    implementation(libs.kafkaSchemaRegistry)
    implementation(libs.kafkaProtobufSerializer)

//    Move version to toml
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")

    implementation(project(":internal-api"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.jNatsServerEmbedded)
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
