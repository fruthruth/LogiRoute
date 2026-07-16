#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

export DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/logiroute_db?useSSL=false&serverTimezone=America/Lima}"
export DB_USERNAME="${DB_USERNAME:-root}"
export DB_PASSWORD="${DB_PASSWORD:-1234abc}"

if [[ -z "${DB_PASSWORD:-}" ]]; then
  read -r -s -p "Contraseña de MySQL para '$DB_USERNAME': " DB_PASSWORD
  echo
  export DB_PASSWORD
fi

if [[ -z "${JWT_SECRET:-}" ]]; then
  JWT_SECRET="$(openssl rand -base64 48)"
  export JWT_SECRET
  echo "JWT_SECRET temporal generado para esta ejecución."
fi

if (( ${#JWT_SECRET} < 32 )); then
  echo "Error: JWT_SECRET debe tener al menos 32 caracteres." >&2
  exit 1
fi

export SERVER_PORT="${SERVER_PORT:-8080}"
export CORS_ALLOWED_ORIGINS="${CORS_ALLOWED_ORIGINS:-http://localhost:8080}"
export LOG_LEVEL="${LOG_LEVEL:-INFO}"
export JPA_SHOW_SQL="${JPA_SHOW_SQL:-false}"
export JPA_DDL_AUTO="${JPA_DDL_AUTO:-update}"
export ERROR_INCLUDE_MESSAGE="${ERROR_INCLUDE_MESSAGE:-never}"

chmod +x ./mvnw
echo "Iniciando LogiRoute en http://localhost:${SERVER_PORT} ..."
exec ./mvnw spring-boot:run
