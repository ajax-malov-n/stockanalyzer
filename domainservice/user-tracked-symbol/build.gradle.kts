plugins {
    `spring-conventions`
}

dependencies {
    implementation(project(":domainservice:core"))
    implementation(project(":internal-api"))
    implementation(project(":domainservice:stock-record"))

    testImplementation(libs.natsTest)
    testImplementation(libs.kafkaTest)
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
