plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlinJvm)
    implementation(libs.detekt)
    implementation(libs.kotlinSpring)
    implementation(libs.springBoot)
    implementation(libs.dependencyManagement)
    implementation(libs.deltaCoverage)
}
