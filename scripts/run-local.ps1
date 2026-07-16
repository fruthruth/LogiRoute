param(
    [string]$DbUrl = "jdbc:mysql://localhost:3306/logiroute_db?useSSL=false&serverTimezone=America/Lima",
    [string]$DbUsername = "root",
    [string]$DbPassword = "1234abc",
    [string]$JwtSecret
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

if ([string]::IsNullOrWhiteSpace($DbPassword)) {
    $securePassword = Read-Host "Contraseña de MySQL para '$DbUsername'" -AsSecureString
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    try {
        $DbPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

if ([string]::IsNullOrWhiteSpace($JwtSecret)) {
    $randomBytes = New-Object byte[] 48
    [Security.Cryptography.RandomNumberGenerator]::Fill($randomBytes)
    $JwtSecret = [Convert]::ToBase64String($randomBytes)
    Write-Host "JWT_SECRET temporal generado para esta ejecución."
}

if ($JwtSecret.Length -lt 32) {
    throw "JWT_SECRET debe tener al menos 32 caracteres."
}

$env:DB_URL = $DbUrl
$env:DB_USERNAME = $DbUsername
$env:DB_PASSWORD = $DbPassword
$env:JWT_SECRET = $JwtSecret
$env:SERVER_PORT = "8080"
$env:CORS_ALLOWED_ORIGINS = "http://localhost:8080"
$env:LOG_LEVEL = "INFO"
$env:JPA_SHOW_SQL = "false"
$env:JPA_DDL_AUTO = "update"
$env:ERROR_INCLUDE_MESSAGE = "never"

Write-Host "Iniciando LogiRoute en http://localhost:8080 ..."
& .\mvnw.cmd spring-boot:run
exit $LASTEXITCODE
