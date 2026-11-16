param([switch]$Build)
$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    if ($Build) {
        podman-compose -f docker/podman-compose.yml build
    }
    podman-compose -f docker/podman-compose.yml up -d
    Write-Host "Frontend: http://localhost:4200"
    Write-Host "Backend:  http://localhost:8080"
} finally {
    Pop-Location
}