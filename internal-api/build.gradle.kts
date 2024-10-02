plugins {
    `kotlin-conventions`
    id("com.google.protobuf") version "0.9.4"
}


dependencies {
    api("com.google.protobuf:protobuf-java:3.24.3")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.3"
    }
}
