param(
    [string]$EnvFile = ".\.env",
    [switch]$NoBuild
)

$ErrorActionPreference = "Stop"

$root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $root

if (-not (Test-Path $EnvFile)) {
    throw "No se encontró $EnvFile. Copia .env.example como .env y completa sus valores."
}

$args = @("compose", "--env-file", $EnvFile, "up", "-d")
if (-not $NoBuild) {
    $args += "--build"
}

Write-Host "Iniciando Innovatech..." -ForegroundColor Cyan
& docker @args
if ($LASTEXITCODE -ne 0) {
    throw "Docker Compose no pudo iniciar la plataforma."
}

Write-Host "`nEstado inicial:" -ForegroundColor Cyan
& docker compose --env-file $EnvFile ps

Write-Host "`nEl primer inicio puede tardar varios minutos mientras compila las imágenes." -ForegroundColor Yellow
Write-Host "Frontend:  http://localhost:5173"
Write-Host "Keycloak:  http://localhost:8085"
Write-Host "Eureka:    http://localhost:8761"
Write-Host "BFF health: http://localhost:8090/actuator/health"
