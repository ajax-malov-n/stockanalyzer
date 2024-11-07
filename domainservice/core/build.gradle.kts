plugins {
    `subproject-spring-conventions`
    `testing-conventions`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    api("org.springframework.boot:spring-boot-starter-data-mongodb")

    api(libs.kafka)

    implementation(project(":internal-api"))

    api(libs.nats)
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
