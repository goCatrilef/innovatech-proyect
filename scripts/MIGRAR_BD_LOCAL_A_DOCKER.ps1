param(
    [string]$EnvFile = ".env",
    [string]$SourceHost = "host.docker.internal",
    [int]$SourcePort = 5433,
    [string]$SourceUser = "postgres",
    [string]$TargetContainer = "innovatech-postgres"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $EnvFile)) {
    throw "No existe $EnvFile. Copia .env.example como .env."
}

$values = @{}
Get-Content $EnvFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
        $key, $value = $line.Split("=", 2)
        $values[$key.Trim()] = $value.Trim()
    }
}

$targetUser = $values["POSTGRES_USER"]
$targetPassword = $values["POSTGRES_PASSWORD"]
if (-not $targetUser -or -not $targetPassword -or $targetPassword -like "CHANGE_ME*") {
    throw "Configura POSTGRES_USER y POSTGRES_PASSWORD en $EnvFile."
}

$secure = Read-Host "Contraseña del PostgreSQL local ($SourceUser@$SourceHost`:$SourcePort)" -AsSecureString
$sourcePassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
)

$dumpDir = Join-Path (Get-Location) "infra/postgres/dumps"
New-Item -ItemType Directory -Force -Path $dumpDir | Out-Null

$databases = @("ms_equipos_db", "ms_proyectos_db", "ms_tareas_db")

foreach ($database in $databases) {
    Write-Host "Exportando $database..." -ForegroundColor Cyan
    & docker run --rm `
        -e "PGPASSWORD=$sourcePassword" `
        -v "${dumpDir}:/dumps" `
        postgres:16-alpine `
        pg_dump -h $SourceHost -p $SourcePort -U $SourceUser -Fc -f "/dumps/$database.dump" $database

    if ($LASTEXITCODE -ne 0) {
        throw "Falló el respaldo de $database."
    }

    & docker cp (Join-Path $dumpDir "$database.dump") "${TargetContainer}:/tmp/$database.dump"
    if ($LASTEXITCODE -ne 0) {
        throw "No fue posible copiar el respaldo de $database al contenedor."
    }

    Write-Host "Restaurando $database..." -ForegroundColor Yellow
    & docker exec -e "PGPASSWORD=$targetPassword" $TargetContainer `
        pg_restore -U $targetUser -d $database --clean --if-exists --no-owner "/tmp/$database.dump"

    if ($LASTEXITCODE -ne 0) {
        throw "Falló la restauración de $database."
    }

    & docker exec $TargetContainer rm -f "/tmp/$database.dump"
    Write-Host "$database migrada correctamente." -ForegroundColor Green
}

$sourcePassword = $null
Write-Host "Migración finalizada. Los respaldos quedaron en infra/postgres/dumps/." -ForegroundColor Green
