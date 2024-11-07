plugins {
    `spring-conventions`
    `grpc-conventions`
    `testing-conventions`
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":domainservice:core"))
    implementation(project(":domainservice:migration"))
    implementation(project(":domainservice:stock-record"))
    implementation(project(":domainservice:user-tracked-symbol"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation(libs.natsTest)
    testImplementation(libs.kafkaTest)
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
