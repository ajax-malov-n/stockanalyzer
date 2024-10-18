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

    implementation(project(":internal-api"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.projectreactor:reactor-core:3.5.11")

    implementation("io.grpc:grpc-core:1.59.0")
    implementation("io.grpc:grpc-protobuf:1.59.0")
    implementation("io.grpc:grpc-netty:1.59.0")
    implementation("io.grpc:grpc-stub:1.59.0")

    implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
    implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")

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
