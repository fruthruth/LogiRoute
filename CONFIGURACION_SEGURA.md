# Configuración segura de LogiRoute

Las credenciales de MySQL y la clave JWT ya no se guardan directamente en `application.properties`.
El sistema requiere las variables `DB_PASSWORD` y `JWT_SECRET` al iniciar.

## Windows PowerShell

La forma recomendada es:

```powershell
.\scripts\run-local.ps1
```

El script solicita la contraseña de MySQL sin mostrarla y genera una clave JWT temporal segura.
También pueden definirse valores explícitos:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/logiroute_db?useSSL=false&serverTimezone=America/Lima"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="TU_PASSWORD"
$env:JWT_SECRET="CLAVE_ALEATORIA_DE_AL_MENOS_32_CARACTERES"
.\mvnw.cmd spring-boot:run
```

## Linux o macOS

```bash
./scripts/run-local.sh
```

O mediante variables:

```bash
export DB_USERNAME="root"
export DB_PASSWORD="TU_PASSWORD"
export JWT_SECRET="CLAVE_ALEATORIA_DE_AL_MENOS_32_CARACTERES"
./mvnw spring-boot:run
```

## Variables disponibles

| Variable | Obligatoria | Valor predeterminado |
|---|---:|---|
| `DB_PASSWORD` | Sí | Ninguno |
| `JWT_SECRET` | Sí | Ninguno; mínimo 32 caracteres |
| `DB_URL` | No | Base local `logiroute_db` |
| `DB_USERNAME` | No | `root` |
| `JWT_EXPIRATION` | No | `86400000` ms |
| `SERVER_PORT` | No | `8080` |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:8080` |
| `LOG_LEVEL` | No | `INFO` |
| `JPA_SHOW_SQL` | No | `false` |
| `ERROR_INCLUDE_MESSAGE` | No | `never` |

## Producción

- Generar un `JWT_SECRET` aleatorio y diferente al de desarrollo.
- No usar el usuario `root` de MySQL.
- Restringir `CORS_ALLOWED_ORIGINS` al dominio real.
- Mantener `JPA_SHOW_SQL=false` y `ERROR_INCLUDE_MESSAGE=never`.
- Gestionar secretos con variables del servidor, Docker Secrets, GitHub Actions Secrets o un gestor de secretos.
