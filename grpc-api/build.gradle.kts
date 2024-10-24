import com.google.protobuf.gradle.id

plugins {
    `kotlin-conventions`
    alias(libs.plugins.protobufPlugin)
    `grpc-conventions`
}

dependencies {
    api(libs.protobuf)
    api(project(":common-proto"))
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    plugins {
        id("grpc") {
            artifact = libs.grpc.get().toString()
        }

        id("reactor-grpc") {
            artifact = libs.grpcReactor.get().toString()
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
