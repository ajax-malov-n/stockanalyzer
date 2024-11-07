plugins {
    `subproject-spring-conventions`
    `testing-conventions`
}

dependencies {
    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)

    implementation(project(":domainservice:core"))
    implementation(project(":domainservice:stock-record"))
    implementation(project(":domainservice:user-tracked-symbol"))

    integrationTestImplementation(project(":domainservice:core"))
    integrationTestImplementation(project(":domainservice:user-tracked-symbol"))
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
