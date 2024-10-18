import com.google.protobuf.gradle.id

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

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.59.0"
        }

        id("reactor-grpc") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.4"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("reactor-grpc")
            }
        }
    }
}
