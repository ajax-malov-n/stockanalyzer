rootProject.name = "stockanalyzer"
include("internal-api")
include("gateway")
include("domainservice")
include("common-proto")
include("grpc-api")
include("domainservice:stock-record")
include("domainservice:user-tracked-symbol")
include("domainservice:core")
include("domainservice:migration")

buildCache {
    local.isEnabled = true
}
