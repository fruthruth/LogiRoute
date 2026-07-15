# AGENTS.md

## Stack

Spring Boot 4.0.6 · Java 21 · MySQL 8 · Thymeleaf · Spring Security + JWT · Maven · Lombok · Guava (cache + rate limiter)

Base package: `com.logiroute.logiroute`

## Build & Run

```bash
./mvnw compile          # compile only
./mvnw spring-boot:run  # run dev server (port 8080)
./mvnw clean package    # full build (skip tests: -DskipTests)
```

Tests: `./mvnw test` — 34 unit tests across 7 files (Mockito-based, no integration tests). `LogirouteApplicationTests.java` is empty and ignored.

## Database

MySQL required. DDL is in `src/main/resources/schema.sql`. Must be run manually:

```bash
mysql -u root -p logiroute_db < src/main/resources/schema.sql
```

`spring.jpa.hibernate.ddl-auto=none` and `spring.sql.init.mode=never` — Hibernate will not create tables and Spring will not run `schema.sql` on startup. Always use `schema.sql` as the source of truth.

## Auth

- Login: `POST /auth/login` with `{"email": "...", "password": "..."}` → returns `{"token": "..."}`.
- JWT sent as `Authorization: Bearer <token>`. The filter (`JwtAuthenticationFilter`) validates tokens and populates `SecurityContext`.
- Roles are derived from `Usuario.rol` enum: `ROLE_ADMINISTRADOR`, `ROLE_REPARTIDOR`, `ROLE_USUARIO`.
- SecurityConfig (`src/main/java/com/logiroute/logiroute/config/SecurityConfig.java`) defines access rules — edit this file when adding new protected endpoints.
- CSRF is **disabled**. Session management is `IF_REQUIRED` (JWT for API, session for Thymeleaf pages).

## Role-Based Routing

| Prefix | Required Role | Notes |
|---|---|---|
| `/admin/**` | `ADMINISTRADOR` | Admin dashboard, pedidos, repartidores |
| `/cliente/**` | `USUARIO` | Client-facing pages |
| `/api/pedidos/**` | `ADMINISTRADOR` | REST API |
| `/api/repartidores/**` | `ADMINISTRADOR` | REST API |
| `/api/asignaciones/**` | `ADMINISTRADOR` | REST API |
| `/api/reportes/**` | `ADMINISTRADOR` | REST API |
| `/api/promociones/**` | `ADMINISTRADOR` | REST API |
| `/api/clientes/**` | `USUARIO` | REST API |
| `/`, `/login`, `/logout`, `/auth/**`, `/css/**`, `/js/**` | Public | |
| `/api/publico/**` | Public | Tracking without login |

## Architecture

Two parallel interfaces: Thymeleaf server-side rendered pages (templates in `src/main/resources/templates/`) + REST controllers returning JSON.

Key controllers: `AuthController` (REST JWT login), `PedidoController`, `RepartidorController`, `AsignacionController`, `ReporteController`, `ClienteController`, `PromocionController`.

Web controllers: `AdminWebController`, `AdminPedidoWebController`, `AdminRepartidorWebController`, `AdminPromocionWebController`, `ClienteWebController`, `AuthWebController`, `HomeController`.

Domain flow: `PENDIENTE → ASIGNADO → EN_RECOJO → EN_TRANSITO → ENTREGADO` (or `CANCELADO`).

## Important Quirks

- Guava `CacheBuilder` and `RateLimiter` beans are in `GuavaConfig` — inject via Spring, not manual instantiation.
- Lombok is excluded from the final JAR but used for compilation. `@RequiredArgsConstructor` is the standard DI pattern.
- Logging goes to both console and `./logs/` (daily rotation, 30-day retention). Package `com.logiroute` logs at DEBUG level.
- No Spring profiles are configured — `application.properties` is the single source of truth. Add profiles yourself if needed.
- `schema.sql` inserts seed data including test users (password: `123456`). Do not modify these if working against the default dev database.
