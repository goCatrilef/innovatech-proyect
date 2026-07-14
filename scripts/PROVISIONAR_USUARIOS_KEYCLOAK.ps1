param(
    [string]$EnvFile = ".\.env",
    [string]$ContainerName = "innovatech-keycloak",
    [string]$Realm = "innovatech"
)

$ErrorActionPreference = "Continue"

# PowerShell 7: no convertir stderr de programas nativos en errores terminantes.
if (Get-Variable PSNativeCommandUseErrorActionPreference -ErrorAction SilentlyContinue) {
    $PSNativeCommandUseErrorActionPreference = $false
}

function Read-DotEnv {
    param([string]$Path)

    if (-not (Test-Path $Path)) {
        throw "No se encontró el archivo .env en: $Path"
    }

    $values = @{}

    foreach ($line in Get-Content $Path) {
        $trimmed = $line.Trim()

        if (-not $trimmed -or $trimmed.StartsWith("#")) {
            continue
        }

        $parts = $trimmed.Split("=", 2)
        if ($parts.Count -ne 2) {
            continue
        }

        $key = $parts[0].Trim()
        $value = $parts[1].Trim().Trim('"').Trim("'")
        $values[$key] = $value
    }

    return $values
}

$cfg = Read-DotEnv -Path $EnvFile

$requiredVariables = @(
    "KEYCLOAK_ADMIN",
    "KEYCLOAK_ADMIN_PASSWORD",
    "DEMO_ADMIN_PASSWORD",
    "DEMO_PO_PASSWORD",
    "DEMO_DEV_PASSWORD"
)

foreach ($name in $requiredVariables) {
    if (-not $cfg.ContainsKey($name) -or [string]::IsNullOrWhiteSpace($cfg[$name])) {
        throw "Falta la variable $name en $EnvFile"
    }
}

$containerExists = docker ps --format "{{.Names}}" 2>$null |
    Where-Object { $_ -eq $ContainerName }

if (-not $containerExists) {
    throw "El contenedor $ContainerName no está ejecutándose."
}

$kcadm = "/opt/keycloak/bin/kcadm.sh"

function Invoke-Kcadm {
    param(
        [Parameter(Mandatory)]
        [string[]]$KcArgs,

        [switch]$CaptureOutput
    )

    # kcadm escribe mensajes informativos en stderr.
    # Se descartan para que PowerShell no los convierta en NativeCommandError.
    $result = & docker exec $ContainerName $kcadm @KcArgs 2>$null
    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0) {
        throw "kcadm falló con código $exitCode. Argumentos: $($KcArgs -join ' ')"
    }

    if ($CaptureOutput) {
        return (($result | Out-String).Trim())
    }
}

Write-Host "Autenticando contra Keycloak..." -ForegroundColor Cyan

Invoke-Kcadm -KcArgs @(
    "config", "credentials",
    "--server", "http://localhost:8080",
    "--realm", "master",
    "--user", $cfg["KEYCLOAK_ADMIN"],
    "--password", $cfg["KEYCLOAK_ADMIN_PASSWORD"]
)

Write-Host "Autenticación administrativa correcta." -ForegroundColor Green

# Asegurar que los roles del realm existan.
foreach ($role in @("ADMIN", "PO", "DEV")) {
    & docker exec $ContainerName $kcadm get "roles/$role" -r $Realm 1>$null 2>$null

    if ($LASTEXITCODE -ne 0) {
        Write-Host "Creando rol $role..."
        Invoke-Kcadm -KcArgs @(
            "create", "roles",
            "-r", $Realm,
            "-s", "name=$role"
        )
    }
}

function Ensure-KeycloakUser {
    param(
        [Parameter(Mandatory)][string]$Username,
        [Parameter(Mandatory)][string]$Password,
        [Parameter(Mandatory)][string]$Role,
        [Parameter(Mandatory)][string]$Email,
        [Parameter(Mandatory)][string]$FirstName,
        [Parameter(Mandatory)][string]$LastName
    )

    $json = Invoke-Kcadm -CaptureOutput -KcArgs @(
        "get", "users",
        "-r", $Realm,
        "-q", "username=$Username",
        "--fields", "id,username"
    )

    $exists = $false

    if (-not [string]::IsNullOrWhiteSpace($json)) {
        try {
            $parsed = ConvertFrom-Json -InputObject $json
            $exists = @($parsed).Count -gt 0
        }
        catch {
            throw "Keycloak devolvió una respuesta JSON inválida al buscar $Username. Respuesta: $json"
        }
    }

    if (-not $exists) {
        Write-Host "Creando usuario $Username..."

        Invoke-Kcadm -KcArgs @(
            "create", "users",
            "-r", $Realm,
            "-s", "username=$Username",
            "-s", "enabled=true",
            "-s", "emailVerified=true",
            "-s", "email=$Email",
            "-s", "firstName=$FirstName",
            "-s", "lastName=$LastName"
        )
    }
    else {
        Write-Host "El usuario $Username ya existe; se actualizará."
    }

    Invoke-Kcadm -KcArgs @(
        "set-password",
        "-r", $Realm,
        "--username", $Username,
        "--new-password", $Password
    )

    Invoke-Kcadm -KcArgs @(
        "add-roles",
        "-r", $Realm,
        "--uusername", $Username,
        "--rolename", $Role
    )

    Write-Host "Usuario $Username listo con rol $Role." -ForegroundColor Green
}

Ensure-KeycloakUser `
    -Username "test.admin" `
    -Password $cfg["DEMO_ADMIN_PASSWORD"] `
    -Role "ADMIN" `
    -Email "test.admin@innovatech.cl" `
    -FirstName "Test" `
    -LastName "Administrador"

Ensure-KeycloakUser `
    -Username "test.po" `
    -Password $cfg["DEMO_PO_PASSWORD"] `
    -Role "PO" `
    -Email "test.po@innovatech.cl" `
    -FirstName "Test" `
    -LastName "Product Owner"

Ensure-KeycloakUser `
    -Username "test.dev" `
    -Password $cfg["DEMO_DEV_PASSWORD"] `
    -Role "DEV" `
    -Email "test.dev@innovatech.cl" `
    -FirstName "Test" `
    -LastName "Desarrollador"

Write-Host ""
Write-Host "Usuarios provisionados:" -ForegroundColor Cyan

& docker exec $ContainerName $kcadm get users `
    -r $Realm `
    --fields username,enabled 2>$null

if ($LASTEXITCODE -ne 0) {
    throw "No fue posible consultar la lista final de usuarios."
}

Write-Host ""
Write-Host "Aprovisionamiento finalizado correctamente." -ForegroundColor Green
