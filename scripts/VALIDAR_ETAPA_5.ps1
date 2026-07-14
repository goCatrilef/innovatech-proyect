param([string]$EnvFile = ".env")

$ErrorActionPreference = "Stop"

Write-Host "1. Estado de los contenedores" -ForegroundColor Cyan
docker compose --env-file $EnvFile -f infra/docker-compose.infra.yml ps

Write-Host "`n2. Bases creadas" -ForegroundColor Cyan
docker exec innovatech-postgres psql -U innovatech -d postgres -tAc "SELECT datname FROM pg_database WHERE datname IN ('ms_equipos_db','ms_proyectos_db','ms_tareas_db','keycloak_db') ORDER BY datname;"

Write-Host "`n3. Realm OpenID Connect" -ForegroundColor Cyan
curl.exe --fail --silent --show-error http://localhost:8085/realms/innovatech/.well-known/openid-configuration | Out-Null
Write-Host "Realm innovatech disponible." -ForegroundColor Green

Write-Host "`n4. Health de Keycloak" -ForegroundColor Cyan
curl.exe --fail --silent --show-error http://localhost:19000/health/ready
