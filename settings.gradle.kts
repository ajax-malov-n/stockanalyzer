rootProject.name = "stockanalyzer"
include("internal-api")
include("gateway")
include("domainservice")


buildCache {
    local.isEnabled = true
}
