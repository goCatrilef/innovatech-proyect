param(
    [string]$EnvFile = ".\.env"
)

$ErrorActionPreference = "Stop"
$root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $root

Write-Host "Deteniendo Innovatech sin borrar los datos..." -ForegroundColor Cyan
& docker compose --env-file $EnvFile down

if ($LASTEXITCODE -ne 0) {
    throw "No fue posible detener la plataforma."
}

Write-Host "Los datos de PostgreSQL se conservaron en innovatech-postgres-data." -ForegroundColor Green
Write-Host "No uses down -v ni docker volume prune." -ForegroundColor Yellow
