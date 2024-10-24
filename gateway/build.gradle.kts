plugins {
    `spring-conventions`
    `grpc-conventions`
}

dependencies {
    implementation(project(":grpc-api"))
    implementation(project(":internal-api"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation(libs.jNats)

    implementation(libs.grpcSpringStarter)
    implementation(libs.grpcServerSpringStarter)

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
