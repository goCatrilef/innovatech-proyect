param(
    [string]$EnvFile = ".\.env"
)

$ErrorActionPreference = "Continue"
$root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $root

function Test-HttpEndpoint {
    param(
        [string]$Name,
        [string]$Url,
        [int[]]$ExpectedStatus = @(200)
    )

    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
        if ($ExpectedStatus -contains [int]$response.StatusCode) {
            Write-Host "[OK] $Name -> $($response.StatusCode)" -ForegroundColor Green
            return $true
        }

        Write-Host "[ERROR] $Name -> $($response.StatusCode)" -ForegroundColor Red
        return $false
    }
    catch {
        $status = $null
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
        }

        if ($status -and ($ExpectedStatus -contains $status)) {
            Write-Host "[OK] $Name -> $status" -ForegroundColor Green
            return $true
        }

        Write-Host "[ERROR] $Name -> $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

Write-Host "Estado de contenedores:" -ForegroundColor Cyan
& docker compose --env-file $EnvFile ps

Write-Host "`nPruebas HTTP:" -ForegroundColor Cyan
$results = @(
    Test-HttpEndpoint "Frontend" "http://localhost:5173/healthz"
    Test-HttpEndpoint "Keycloak" "http://localhost:8085/realms/innovatech/.well-known/openid-configuration"
    Test-HttpEndpoint "Eureka" "http://localhost:8761/actuator/health"
    Test-HttpEndpoint "ms-equipos" "http://localhost:8081/actuator/health"
    Test-HttpEndpoint "ms-proyectos" "http://localhost:8082/actuator/health"
    Test-HttpEndpoint "ms-tareas" "http://localhost:8083/actuator/health"
    Test-HttpEndpoint "API Gateway" "http://localhost:8080/actuator/health"
    Test-HttpEndpoint "BFF" "http://localhost:8090/actuator/health"
    Test-HttpEndpoint "BFF protegido sin token" "http://localhost:8090/api/bff/proyectos" @(401)
)

if ($results -contains $false) {
    Write-Host "`nHay validaciones pendientes. Revisa: docker compose logs --tail 200 <servicio>" -ForegroundColor Yellow
    exit 1
}

Write-Host "`nPlataforma validada correctamente." -ForegroundColor Green
