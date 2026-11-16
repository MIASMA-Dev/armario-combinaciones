$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    podman-compose -f docker/podman-compose.yml down
} finally {
    Pop-Location
}