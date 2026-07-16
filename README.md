# LogiRoute — Sistema de Logística, Rutas y Seguimiento de Pedidos

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C00.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

**LogiRoute** es una plataforma web de gestión logística integral que permite administrar pedidos, asignar repartidores, definir rutas y hacer seguimiento operativo de entregas. Desarrollada con **Spring Boot 4**, utiliza **Spring Security + JWT** para autenticación por roles, **Spring Data JPA** para persistencia, **Thymeleaf** para las vistas y **Logback** para auditoría de trazas.

---

## Tabla de Contenidos

- [Características](#características)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Configuración de la Base de Datos](#configuración-de-la-base-de-datos)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Roles y Accesos](#roles-y-accesos)
- [Módulos del Sistema](#módulos-del-sistema)
- [Configuración de Logback](#configuración-de-logback)
- [Estructura de Carpetas](#estructura-de-carpetas)
- [Equipo](#equipo)

---

## Características

- **Gestión de Pedidos:** Registro, seguimiento y control del ciclo de vida completo de envíos (PENDIENTE → ASIGNADO → EN_RECOJO → EN_TRANSITO → ENTREGADO).
- **Asignación Inteligente:** Emparejamiento eficiente de repartidores, vehículos y rutas disponibles.
- **Control de Accesos por Roles:** Autenticación con Spring Security y JWT para tres perfiles: Administrador, Repartidor y Cliente.
- **Seguimiento de Entregas:** Los clientes pueden rastrear sus pedidos mediante un código único.
- **Registro de Incidentes:** Categorización automática de contratiempos (RETRASO, DAÑO, ROBO, DIRECCIÓN_INCORRECTA, CLIENTE_AUSENTE).
- **Auditoría con Logback:** Trazas operativas guardadas en archivos de log con rotación diaria por hasta 30 días.
- **Rate Limiting:** Control de peticiones por segundo para proteger la API.

---

## Arquitectura del Proyecto

El sistema sigue la arquitectura de capas estándar de Spring Boot:

```
Capa de Presentación  →  Thymeleaf (HTML) + REST Controllers
         ↓
Capa de Negocio       →  Services (AsignacionService, PedidoService, etc.)
         ↓
Capa de Persistencia  →  Repositories (Spring Data JPA)
         ↓
Base de Datos         →  MySQL 8 (logiroute_db)
```

**Separación de responsabilidades:**

| Paquete | Responsabilidad |
|---|---|
| `controller/` | Manejo de peticiones HTTP (Web y REST) |
| `service/` | Lógica de negocio |
| `repository/` | Acceso a datos con Spring Data JPA |
| `model/` | Entidades JPA (tablas de la BD) |
| `dto/` | Objetos de transferencia de datos |
| `security/` | JWT Filter, JwtUtil, CustomUserDetailsService |
| `config/` | SecurityConfig, GuavaConfig |
| `utils/` | Generador de códigos únicos de pedido |

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework base |
| Spring Security | (incluido) | Autenticación y autorización |
| Spring Data JPA | (incluido) | ORM y acceso a datos |
| Thymeleaf | (incluido) | Motor de plantillas HTML |
| MySQL Connector | (incluido) | Driver de base de datos |
| JJWT | 0.12.6 | Generación y validación de tokens JWT |
| Google Guava | 33.3.1-jre | Rate limiting de autenticación |
| Lombok | (incluido) | Reducción de código boilerplate |
| Logback | (incluido vía Spring) | Sistema de logging y auditoría |
| Maven | 3.x | Gestión de dependencias y build |

---

## Requisitos Previos

Antes de ejecutar el proyecto asegúrate de tener instalado:

- **Java 21** o superior → [Descargar](https://adoptium.net/)
- **MySQL 8** → [Descargar](https://dev.mysql.com/downloads/)
- **Maven 3.x** (opcional si usas el wrapper `./mvnw`)
- **Git**

Verificar versiones instaladas:

```bash
java -version
mysql --version
mvn -version
```

---

## Configuración de la Base de Datos

**1. Crear la base de datos en MySQL:**

```sql
CREATE DATABASE logiroute_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**2. Ejecutar el script de esquema:**

```bash
mysql -u root -p logiroute_db < src/main/resources/schema.sql
```

Esto creará todas las tablas e insertará los datos iniciales (usuarios de prueba con contraseña `123456`).

**3. Configurar las credenciales mediante variables de entorno:**

`application.properties` no almacena contraseñas ni una clave JWT fija. En Windows puedes usar el iniciador incluido:

```powershell
.\scripts\run-local.ps1
```

El script solicita la contraseña de MySQL sin mostrarla y genera una clave JWT temporal segura. También puedes definir las variables manualmente:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="TU_PASSWORD"
$env:JWT_SECRET="CLAVE_ALEATORIA_DE_AL_MENOS_32_CARACTERES"
```

Consulta `CONFIGURACION_SEGURA.md` para Linux, macOS y configuración de producción.

---

## Instalación y Ejecución

**1. Clonar el repositorio:**

```bash
git clone https://github.com/fruthruth/LogiRoute.git
cd LogiRoute
```

**2. Ejecutar mediante el iniciador seguro:**

```powershell
# Windows PowerShell
.\scripts\run-local.ps1
```

```bash
# Linux/macOS
./scripts/run-local.sh
```

Los iniciadores cargan las variables requeridas y ejecutan Maven Wrapper. Para una configuración manual, revisa `CONFIGURACION_SEGURA.md`.

**3. Acceder a la aplicación:**

Abre el navegador en:

```
http://localhost:8080
```

**4. Credenciales de prueba:**

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | admin@logiroute.com | 123456 |
| Cliente | maria.gutierrez@gmail.com | 123456 |
| Repartidor | juan.ramirez@logiroute.com | 123456 |

---

## Roles y Accesos

El sistema maneja tres roles con vistas y permisos diferenciados:

**ADMINISTRADOR**
- Dashboard general del sistema
- Gestión completa de pedidos (`/admin/pedidos`)
- Gestión de repartidores (`/admin/repartidores`)
- Generación de reportes y exportación a Excel
- Consulta centralizada de incidentes (`/admin/incidentes`)

**REPARTIDOR**
- Visualización de pedidos asignados
- Actualización de estado de entrega
- Registro de incidentes en ruta

**USUARIO (Cliente)**
- Seguimiento de pedidos por código
- Historial de envíos

---

## Módulos del Sistema

### Pedidos
Gestiona el ciclo de vida completo de un envío. Los estados posibles son:

```
PENDIENTE → ASIGNADO → EN_RECOJO → EN_TRANSITO → ENTREGADO
                                               ↘ CANCELADO
```

### Asignación
El `AsignacionService` aplica reglas operativas antes de asignar un pedido:

1. El pedido debe estar `PENDIENTE` o `ASIGNADO` para una asignación manual.
2. El repartidor debe estar `DISPONIBLE` y su cuenta debe permanecer activa.
3. Debe existir un vehículo operativo asociado al repartidor.
4. La carga activa acumulada más el peso del nuevo pedido no puede superar la capacidad del vehículo.
5. La autoasignación prioriza menor cantidad de pedidos activos, menor peso acumulado y menor distancia al destino cuando existen coordenadas.
6. Una reasignación actualiza la entrega existente en lugar de crear un registro duplicado.

La API también expone `POST /api/asignaciones/auto/{pedidoId}` para ejecutar la selección automática.

### Entregas
Registra los detalles operativos de cada entrega: fecha de recojo, fecha de entrega, firma y foto como evidencia.

### Incidentes
Ante cualquier contratiempo, el repartidor puede registrar un incidente desde sus pedidos activos. El administrador consulta el historial consolidado en `/admin/incidentes`. Tipos soportados: `RETRASO`, `DANO`, `ROBO`, `DIRECCION_INCORRECTA`, `CLIENTE_AUSENTE`.

### Reportes
El `ReporteController` y `ReporteService` permiten filtrar pedidos y exportar el resultado operativo a Excel mediante Apache POI.

---

## Pruebas automatizadas

El proyecto contiene **59 pruebas unitarias** distribuidas en **9 archivos**, con JUnit 5 y Mockito. Incluyen CRUD, transiciones de estado, asignación inteligente, promociones, reportes, clientes, generación de códigos e incidentes.

```bash
./mvnw test
```

La ejecución local debe finalizar sin fallos antes de generar el entregable.

---

## Configuración de Logback

LogiRoute implementa auditoría técnica completa usando **Logback** configurado en `logback-spring.xml`. Los logs se generan simultáneamente en consola y en archivos con rotación diaria.

**Configuración (`src/main/resources/logback-spring.xml`):**

```xml
<!-- Archivos de log en carpeta ./logs/ -->
<!-- Rotación diaria: logiroute-YYYY-MM-DD.log -->
<!-- Retención máxima: 30 días -->
<!-- Nivel DEBUG para el paquete com.logiroute -->
```

**Ubicación de los archivos generados:**

```
logs/
├── logiroute.log               ← Log activo actual
└── logiroute-2026-06-05.log    ← Logs históricos rotados
```

**Niveles de log usados:**

| Nivel | Uso |
|---|---|
| `DEBUG` | Detalle de operaciones internas del paquete `com.logiroute` |
| `INFO` | Eventos generales del sistema |
| `WARN` | Situaciones inesperadas no críticas |
| `ERROR` | Errores que requieren atención |

---

## Estructura de Carpetas

```
LogiRoute/
├── logs/                                   # Archivos de auditoría (Logback)
├── src/
│   ├── main/
│   │   ├── java/com/logiroute/logiroute/
│   │   │   ├── config/                     # SecurityConfig, GuavaConfig
│   │   │   ├── controller/                 # Controllers Web y REST
│   │   │   ├── dto/                        # DTOs de transferencia
│   │   │   ├── model/                      # Entidades JPA
│   │   │   ├── repository/                 # Repositorios Spring Data
│   │   │   ├── security/                   # JWT, UserDetailsService
│   │   │   ├── service/                    # Lógica de negocio
│   │   │   ├── utils/                      # Utilidades (CodigoGenerator)
│   │   │   └── LogirouteApplication.java   # Clase principal
│   │   └── resources/
│   │       ├── static/css/                 # Estilos CSS
│   │       ├── templates/                  # Vistas Thymeleaf (HTML)
│   │       │   ├── admin/
│   │       │   ├── auth/
│   │       │   └── cliente/
│   │       ├── application.properties      # Configuración externa por variables
│   │       ├── logback-spring.xml          # Configuración de logging
│   │       └── schema.sql                  # Script de base de datos
│   └── test/                               # Tests unitarios
├── pom.xml                                 # Dependencias Maven
└── README.md
```

---

## Equipo

|       Integrantes                 |     GitHub              |
|-----------------------------------|-------------------------|
| Chambi Mamani, Sebastián          | [@Walffetx] |
| Coasaca Cconislla, Saul Teodoro   | [@asulcc]   |
| Huamaní Fernández Ruth Nayaly     | [@fruthruth](https://github.com/fruthruth) |
| Palomino Valeriano, Anthony Oliver| [@anthonypalomino24]|
---

> Proyecto académico desarrollado con fines educativos — Universidad, 2026.
