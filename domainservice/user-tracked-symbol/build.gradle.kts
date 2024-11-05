plugins {
    `spring-conventions`
}

dependencies {
    implementation(project(":domainservice:core"))
    implementation(project(":internal-api"))
    implementation(project(":domainservice:stock-record"))
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
