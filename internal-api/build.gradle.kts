dependencies {
    implementation("com.google.protobuf:protobuf-java:3.24.3")
    implementation("com.google.protobuf:protobuf-java-util:4.28.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.3"
    }
}