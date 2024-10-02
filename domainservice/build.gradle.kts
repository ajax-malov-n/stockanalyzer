plugins {
    `spring-conventions`
    `delta-coverage-conventions`
}

dependencies {
    implementation(libs.finnhub.kotlin.client)

    implementation("io.nats:jnats:2.16.14")

    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)

    implementation(libs.shedlock.spring)
    implementation(libs.shedlock.provider.mongo)

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation(project(":internal-api"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation(libs.mockito.kotlin)
    testImplementation("berlin.yuna:nats-server-embedded:2.10.22-rc.2")
    testImplementation("io.projectreactor:reactor-test:3.5.11")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

tasks.named("check") {
    dependsOn("deltaCoverage")
    dependsOn("detektMain")
    dependsOn("detektTest")
}
