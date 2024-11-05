plugins {
    `spring-conventions`
}

dependencies {
    implementation(libs.mongock.springboot.v3)
    implementation(libs.mongock.mongodb.springdata.v4.driver)

    implementation(project(":domainservice:core"))
    implementation(project(":domainservice:stock-record"))
    implementation(project(":domainservice:user-tracked-symbol"))
}

tasks.named("check") {
    dependsOn("detektMain")
    dependsOn("detektTest")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    enabled = false
}
