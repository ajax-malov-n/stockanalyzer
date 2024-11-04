plugins {
    `spring-conventions`
}

dependencies {
    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation(project(":domainservice:core"))
    implementation(project(":domainservice:stock-record"))
    implementation(project(":domainservice:user-tracked-symbol"))
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
