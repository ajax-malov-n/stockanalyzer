plugins {
    `kotlin-conventions`
    alias(libs.plugins.protobufPlugin)
}

dependencies {
    api(libs.protobuf)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }
}
