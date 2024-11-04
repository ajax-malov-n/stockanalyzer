plugins {
    `spring-conventions`
}

dependencies {
    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)

    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    api("org.springframework.boot:spring-boot-starter-data-mongodb")

    api(libs.kafka)

    implementation(project(":internal-api"))

    api(libs.nats)

    api("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(libs.jacksonJsr310)
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}
