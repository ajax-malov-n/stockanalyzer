import systems.ajax.malov.stockanalyzer.libDeps

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libDeps.reactorCore)

    implementation(libDeps.grpcCore)
    implementation(libDeps.grpcProtobuf)
    implementation(libDeps.grpcNetty)
    implementation(libDeps.grpcStub)

    implementation(libDeps.grpcReactor)
    implementation(libDeps.reactiveGrpcCommon)
    implementation(libDeps.reactorGrpcStub)
}
