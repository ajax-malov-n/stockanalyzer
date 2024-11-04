rootProject.name = "stockanalyzer"
include("internal-api")
include("gateway")
include("domainservice")
include("common-proto")
include("grpc-api")


buildCache {
    local.isEnabled = true
}
include("domainservice:stock-record")
findProject(":domainservice:stock-record")?.name = "stock-record"
include("domainservice:user-tracked-symbol")
findProject(":domainservice:user-tracked-symbol")?.name = "user-tracked-symbol"
include("domainservice:core")
findProject(":domainservice:core")?.name = "core"
include("domainservice:migration")
findProject(":domainservice:migration")?.name = "migration"
