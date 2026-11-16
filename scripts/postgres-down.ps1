param(
    [string]$Container = "armario-postgres",
    [switch]$Remove
)

if ($Remove) {
    podman rm -f $Container 2>$null | Out-Null
    Write-Host "Contenedor $Container eliminado (el volumen de datos se conserva)"
} else {
    podman stop $Container 2>$null | Out-Null
    Write-Host "PostgreSQL detenido ($Container)"
}
