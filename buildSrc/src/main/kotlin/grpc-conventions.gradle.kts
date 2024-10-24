import gradle.kotlin.dsl.accessors._9f7f456724d17774e707e28dbf6a22c7.implementation
import systems.ajax.malov.stockanalyzer.libDeps

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
