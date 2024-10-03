plugins {
    `kotlin-conventions`
    alias(libs.plugins.protobufPlugin)
}


dependencies {
    api(libs.protobuf)
}
//artifact = libs.protoc.get().toString()

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }
}
