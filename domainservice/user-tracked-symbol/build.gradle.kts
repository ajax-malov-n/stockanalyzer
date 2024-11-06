plugins {
    `subproject-spring-conventions`
    `testing-conventions`
}

dependencies {
    implementation(project(":domainservice:core"))
    implementation(project(":internal-api"))
    implementation(project(":domainservice:stock-record"))

    integrationTestImplementation(project(":domainservice:core"))
    integrationTestImplementation(project(":domainservice:stock-record"))
    integrationTestImplementation(testFixtures(project(":domainservice:user-tracked-symbol")))
    integrationTestImplementation(testFixtures(project(":domainservice:stock-record")))
    integrationTestImplementation(libs.natsTest)
    integrationTestImplementation(libs.kafkaTest)
    integrationTestImplementation(project(":internal-api"))
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
