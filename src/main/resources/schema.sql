-- ============================================================
-- LogiRoute - Script de Base de Datos
-- Compatible con: MySQL 8+
-- Ejecutar en la DB logiroute_db
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE usuarios (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    rol         VARCHAR(20)  NOT NULL DEFAULT 'USUARIO',
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_email (email),
    CHECK (rol IN ('ADMINISTRADOR', 'REPARTIDOR', 'USUARIO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: administradores
-- ============================================================
CREATE TABLE administradores (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id    BIGINT       NOT NULL,
    departamento  VARCHAR(100) NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_usuario (usuario_id),
    CONSTRAINT fk_admin_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: repartidores
-- ============================================================
CREATE TABLE repartidores (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT       NOT NULL,
    telefono    VARCHAR(20)  NOT NULL,
    licencia    VARCHAR(20)  NOT NULL,
    estado      VARCHAR(20)  NOT NULL DEFAULT 'DISPONIBLE',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_repartidor_usuario (usuario_id),
    UNIQUE KEY uk_repartidor_licencia (licencia),
    CONSTRAINT fk_repartidor_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CHECK (estado IN ('DISPONIBLE', 'EN_RUTA', 'INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: clientes
-- ============================================================
CREATE TABLE clientes (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT       NOT NULL,
    telefono    VARCHAR(20)  NOT NULL,
    direccion   VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_usuario (usuario_id),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: vehiculos
-- ============================================================
CREATE TABLE vehiculos (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    placa             VARCHAR(20)   NOT NULL,
    marca             VARCHAR(50)   NOT NULL,
    modelo            VARCHAR(50)   NOT NULL,
    anio              INT           NOT NULL,
    capacidad_kg      DECIMAL(10,2) NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'DISPONIBLE',
    repartidor_id     BIGINT        NULL,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_vehiculo_placa (placa),
    CONSTRAINT fk_vehiculo_repartidor FOREIGN KEY (repartidor_id) REFERENCES repartidores (id),
    CHECK (estado IN ('DISPONIBLE', 'EN_USO', 'EN_MANTENIMIENTO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: rutas
-- ============================================================
CREATE TABLE rutas (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    nombre               VARCHAR(100)  NOT NULL,
    origen               VARCHAR(255)  NOT NULL,
    destino              VARCHAR(255)  NOT NULL,
    distancia_km         DECIMAL(10,2) NOT NULL,
    tiempo_estimado_min  DECIMAL(10,2) NOT NULL,
    activa               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: pedidos
-- ============================================================
CREATE TABLE pedidos (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(20)   NOT NULL,
    cliente_id          BIGINT        NOT NULL,
    repartidor_id       BIGINT        NULL,
    ruta_id             BIGINT        NULL,
    direccion_origen    VARCHAR(255)  NOT NULL,
    direccion_destino   VARCHAR(255)  NOT NULL,
    peso                DECIMAL(10,2) NOT NULL,
    tipo_paquete        VARCHAR(50)   NOT NULL,
    estado              VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    costo               DECIMAL(10,2) NULL,
    fecha_estimada      DATETIME      NULL,
    fecha_entrega       DATETIME      NULL,
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pedido_codigo (codigo),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT fk_pedido_repartidor FOREIGN KEY (repartidor_id) REFERENCES repartidores (id),
    CONSTRAINT fk_pedido_ruta FOREIGN KEY (ruta_id) REFERENCES rutas (id),
    CHECK (estado IN ('PENDIENTE', 'ASIGNADO', 'EN_RECOJO', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: entregas
-- ============================================================
CREATE TABLE entregas (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    pedido_id       BIGINT       NOT NULL,
    repartidor_id   BIGINT       NOT NULL,
    fecha_recojo    DATETIME     NULL,
    fecha_entrega   DATETIME     NULL,
    firma           VARCHAR(200) NULL,
    foto            VARCHAR(500) NULL,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_entrega_pedido (pedido_id),
    CONSTRAINT fk_entrega_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT fk_entrega_repartidor FOREIGN KEY (repartidor_id) REFERENCES repartidores (id),
    CHECK (estado IN ('PENDIENTE', 'EN_CAMINO', 'ENTREGADO', 'FALLIDO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: incidentes
-- ============================================================
CREATE TABLE incidentes (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    pedido_id     BIGINT       NOT NULL,
    tipo          VARCHAR(30)  NOT NULL,
    descripcion   VARCHAR(500) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_incidente_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CHECK (tipo IN ('RETRASO', 'DANO', 'ROBO', 'DIRECCION_INCORRECTA', 'CLIENTE_AUSENTE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DATOS INICIALES
-- ============================================================

-- Usuario admin (password: 123456 con BCrypt 10 rondas)
INSERT INTO usuarios (nombre, email, password, rol, activo)
VALUES ('Administrador', 'admin@logiroute.com',
        '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS',
        'ADMINISTRADOR', TRUE);

-- Registro del admin en tabla administradores
INSERT INTO administradores (usuario_id, departamento)
VALUES (1, 'Sistemas');

-- Usuario cliente de prueba (password: 123456)
INSERT INTO usuarios (nombre, email, password, rol, activo)
VALUES ('Cliente Test', 'cliente@test.com',
        '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS',
        'USUARIO', TRUE);

INSERT INTO clientes (usuario_id, telefono, direccion)
VALUES (2, '999888777', 'Av. Principal 123, Lima');

-- Usuario repartidor de prueba (password: 123456)
INSERT INTO usuarios (nombre, email, password, rol, activo)
VALUES ('Repartidor Test', 'repartidor@test.com',
        '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS',
        'REPARTIDOR', TRUE);

INSERT INTO repartidores (usuario_id, telefono, licencia, estado)
VALUES (3, '988777666', 'LIC-001', 'DISPONIBLE');

-- Ruta de prueba
INSERT INTO rutas (nombre, origen, destino, distancia_km, tiempo_estimado_min, activa)
VALUES ('Ruta Centro-Lima', 'Av. Principal 123', 'Jr. Comercio 456', 15.50, 35.00, TRUE);

SET FOREIGN_KEY_CHECKS = 1;
