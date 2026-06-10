# LogiRoute — Sistema de Logística, Rutas y Seguimiento de Pedidos

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C00.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

**LogiRoute** es una plataforma web de gestión logística integral que permite administrar pedidos, asignar repartidores, definir rutas y hacer seguimiento de entregas en tiempo real. Desarrollada con **Spring Boot 4**, utiliza **Spring Security + JWT** para autenticación por roles, **Spring Data JPA** para persistencia, **Thymeleaf** para las vistas y **Logback** para auditoría de trazas.

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
- **Caché con Guava:** Reducción de carga en consultas frecuentes usando Google Guava Cache.
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
| Google Guava | 33.3.1-jre | Caché en memoria y rate limiting |
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

**3. Verificar la conexión en `application.properties`:**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logiroute_db?useSSL=false&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=root
```

> Cambia `username` y `password` según tu configuración local de MySQL.

---

## Instalación y Ejecución

**1. Clonar el repositorio:**

```bash
git clone https://github.com/fruthruth/LogiRoute.git
cd LogiRoute
```

**2. Compilar y ejecutar con Maven Wrapper:**

```bash
# En Linux/Mac
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

**3. Acceder a la aplicación:**

Abre el navegador en:

```
http://localhost:8080
```

**4. Credenciales de prueba:**

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | admin@logiroute.com | 123456 |
| Cliente | cliente@test.com | 123456 |
| Repartidor | repartidor@test.com | 123456 |

---

## Roles y Accesos

El sistema maneja tres roles con vistas y permisos diferenciados:

**ADMINISTRADOR**
- Dashboard general del sistema
- Gestión completa de pedidos (`/admin/pedidos`)
- Gestión de repartidores (`/admin/repartidores`)
- Generación de reportes

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
El `AsignacionService` se encarga de emparejar un pedido con el repartidor disponible más adecuado y asignarle un vehículo y ruta.

### Entregas
Registra los detalles operativos de cada entrega: fecha de recojo, fecha de entrega, firma y foto como evidencia.

### Incidentes
Ante cualquier contratiempo, el sistema registra el tipo de incidente vinculado al pedido afectado. Tipos soportados: `RETRASO`, `DANO`, `ROBO`, `DIRECCION_INCORRECTA`, `CLIENTE_AUSENTE`.

### Reportes
El `ReporteController` y `ReporteService` exponen datos agregados para visualización en el dashboard administrativo.

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
│   │       ├── application.properties      # Configuración de la app
│   │       ├── logback-spring.xml          # Configuración de logging
│   │       └── schema.sql                  # Script de base de datos
│   └── test/                               # Tests unitarios
├── pom.xml                                 # Dependencias Maven
└── README.md
```

---

## Equipo

| Integrante | GitHub |
|---|---|
| Ruth | [@fruthruth](https://github.com/fruthruth) |
| _(agregar integrantes)_ | — |

---

> Proyecto académico desarrollado con fines educativos — Universidad, 2026.
