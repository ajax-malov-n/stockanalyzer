plugins {
    `kotlin-conventions`
    `spring-conventions`
}

dependencies {
    implementation(project(":internal-api"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("io.nats:jnats:2.16.14")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
