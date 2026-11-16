param(
    [string]$Container = "armario-postgres",
    [string]$Volume = "armario-pgdata",
    [int]$Port = 5432
)

$exists = podman container exists $Container 2>$null
if ($LASTEXITCODE -eq 0) {
    $running = podman inspect -f "{{.State.Running}}" $Container
    if ($running -eq "true") {
        Write-Host "PostgreSQL ya esta en marcha ($Container, puerto $Port)"
        exit 0
    }
    podman start $Container | Out-Null
    Write-Host "PostgreSQL arrancado ($Container, puerto $Port)"
    exit 0
}

podman run -d --name $Container `
    -e POSTGRES_DB=armario `
    -e POSTGRES_USER=armario `
    -e POSTGRES_PASSWORD=armario `
    -p "${Port}:5432" `
    -v "${Volume}:/var/lib/postgresql/data" `
    --restart unless-stopped `
    docker.io/library/postgres:16-alpine | Out-Null

Write-Host "PostgreSQL creado ($Container, puerto $Port, volumen $Volume)"
Write-Host "Backend: mvn spring-boot:run -Dspring-boot.run.profiles=dev,postgres"
