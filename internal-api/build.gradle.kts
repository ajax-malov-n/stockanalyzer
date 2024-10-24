plugins {
    `kotlin-conventions`
    alias(libs.plugins.protobufPlugin)
}

dependencies {
    api(libs.protobuf)
    api(project(":common-proto"))
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }
}
