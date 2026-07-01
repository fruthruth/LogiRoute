-- ============================================================
-- LogiRoute - Script Completo de Base de Datos + Datos de Prueba
-- Compatible con: MySQL 8+
-- Ejecutar: mysql -u root -p logiroute_db < schema-completo.sql
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
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
CREATE TABLE IF NOT EXISTS administradores (
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
CREATE TABLE IF NOT EXISTS repartidores (
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
CREATE TABLE IF NOT EXISTS clientes (
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
CREATE TABLE IF NOT EXISTS vehiculos (
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
CREATE TABLE IF NOT EXISTS rutas (
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
CREATE TABLE IF NOT EXISTS pedidos (
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
CREATE TABLE IF NOT EXISTS entregas (
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
CREATE TABLE IF NOT EXISTS incidentes (
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
-- TABLA: promociones
-- ============================================================
CREATE TABLE IF NOT EXISTS promociones (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    titulo                  VARCHAR(100)  NOT NULL,
    descripcion             VARCHAR(500)  NOT NULL,
    descuento_porcentaje    DECIMAL(5,2)  NOT NULL,
    monto_minimo            DECIMAL(10,2) NULL,
    fecha_inicio            DATETIME      NOT NULL,
    fecha_fin               DATETIME      NOT NULL,
    activa                  BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at              DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME      NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- ------------------------------------------------------------
-- USUARIOS (1 admin + 15 repartidores + 14 clientes = 30)
-- Password de todos: 123456 (BCrypt hash)
-- ------------------------------------------------------------
INSERT INTO usuarios (id, nombre, email, password, rol, activo) VALUES
-- Admin
(1,  'Carlos Mendoza',    'admin@logiroute.com',       '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'ADMINISTRADOR', TRUE),
-- Repartidores (2-16)
(2,  'Juan Ramirez',      'juan.ramirez@logiroute.com',   '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(3,  'Miguel Torres',     'miguel.torres@logiroute.com',  '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(4,  'Luis Fernandez',    'luis.fernandez@logiroute.com', '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(5,  'Carlos Ruiz',       'carlos.ruiz@logiroute.com',    '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(6,  'Pedro Sanchez',     'pedro.sanchez@logiroute.com',  '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(7,  'Andres Lopez',      'andres.lopez@logiroute.com',   '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(8,  'Jorge Garcia',      'jorge.garcia@logiroute.com',   '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(9,  'Marco Diaz',        'marco.diaz@logiroute.com',     '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(10, 'Roberto Morales',   'roberto.morales@logiroute.com','$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(11, 'Fernando Vargas',   'fernando.vargas@logiroute.com','$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(12, 'Diego Castillo',    'diego.castillo@logiroute.com', '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(13, 'Ricardo Flores',    'ricardo.flores@logiroute.com', '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(14, 'Oscar Medina',      'oscar.medina@logiroute.com',   '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(15, 'Alberto Reyes',     'alberto.reyes@logiroute.com',  '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
(16, 'Victor Huamán',     'victor.huaman@logiroute.com',  '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'REPARTIDOR', TRUE),
-- Clientes (17-30)
(17, 'Maria Gutierrez',   'maria.gutierrez@gmail.com',    '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(18, 'Ana Torres',        'ana.torres@gmail.com',         '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(19, 'Rosa Martinez',     'rosa.martinez@hotmail.com',    '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(20, 'Lucia Fernandez',   'lucia.fernandez@gmail.com',    '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(21, 'Carmen Lopez',      'carmen.lopez@hotmail.com',     '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(22, 'Patricia Ramos',    'patricia.ramos@gmail.com',     '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(23, 'Claudia Morales',   'claudia.morales@gmail.com',    '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(24, 'Sandra Castillo',   'sandra.castillo@hotmail.com',  '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(25, 'Elena Vargas',      'elena.vargas@gmail.com',       '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(26, 'Teresa Ruiz',       'teresa.ruiz@gmail.com',        '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(27, 'Jessica Flores',    'jessica.flores@hotmail.com',   '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(28, 'Vanessa Garcia',    'vanessa.garcia@gmail.com',     '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(29, 'Diana Medina',      'diana.medina@gmail.com',       '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE),
(30, 'Gloria Sanchez',    'gloria.sanchez@hotmail.com',   '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS', 'USUARIO', TRUE);

-- ------------------------------------------------------------
-- ADMINISTRADOR
-- ------------------------------------------------------------
INSERT INTO administradores (usuario_id, departamento) VALUES
(1, 'Sistemas');

-- ------------------------------------------------------------
-- REPARTIDORES (15)
-- ------------------------------------------------------------
INSERT INTO repartidores (id, usuario_id, telefono, licencia, estado) VALUES
(1,  2,  '991234567', 'LIC-001', 'DISPONIBLE'),
(2,  3,  '992345678', 'LIC-002', 'DISPONIBLE'),
(3,  4,  '993456789', 'LIC-003', 'EN_RUTA'),
(4,  5,  '994567890', 'LIC-004', 'DISPONIBLE'),
(5,  6,  '995678901', 'LIC-005', 'DISPONIBLE'),
(6,  7,  '996789012', 'LIC-006', 'EN_RUTA'),
(7,  8,  '997890123', 'LIC-007', 'DISPONIBLE'),
(8,  9,  '998901234', 'LIC-008', 'DISPONIBLE'),
(9,  10, '999012345', 'LIC-009', 'INACTIVO'),
(10, 11, '981123456', 'LIC-010', 'DISPONIBLE'),
(11, 12, '982234567', 'LIC-011', 'EN_RUTA'),
(12, 13, '983345678', 'LIC-012', 'DISPONIBLE'),
(13, 14, '984456789', 'LIC-013', 'DISPONIBLE'),
(14, 15, '985567890', 'LIC-014', 'DISPONIBLE'),
(15, 16, '986678901', 'LIC-015', 'EN_RUTA');

-- ------------------------------------------------------------
-- CLIENTES (14)
-- ------------------------------------------------------------
INSERT INTO clientes (id, usuario_id, telefono, direccion) VALUES
(1,  17, '998111222', 'Av. Javier Prado Este 4200, Santiago de Surco'),
(2,  18, '998222333', 'Jr. de la Unión 678, Cercado de Lima'),
(3,  19, '998333444', 'Av. Arequipa 1455, Lince'),
(4,  20, '998444555', 'Av. La Marina 234, San Miguel'),
(5,  21, '998555666', 'Av. Brasil 2100, Magdalena del Mar'),
(6,  22, '998666777', 'Calle Los Olivos 567, Los Olivos'),
(7,  23, '998777888', 'Av. Angamos 1234, Surquillo'),
(8,  24, '998888999', 'Jr. Tacna 456, Cercado de Lima'),
(9,  25, '998999000', 'Av. Benavides 890, Miraflores'),
(10, 26, '997111222', 'Av. Rivera Navarrete 650, San Isidro'),
(11, 27, '997222333', 'Calle Schell 345, Miraflores'),
(12, 28, '997333444', 'Av. Aviación 1800, San Borja'),
(13, 29, '997444555', 'Av. Guardia Civil 500, Chorrillos'),
(14, 30, '997555666', 'Jr. Carabayllo 123, Santa Anita');

-- ------------------------------------------------------------
-- VEHICULOS (15, uno por repartidor)
-- ------------------------------------------------------------
INSERT INTO vehiculos (id, placa, marca, modelo, anio, capacidad_kg, estado, repartidor_id) VALUES
(1,  'ABC-123', 'Honda',    'Wave 110',    2023, 10.00, 'EN_USO',       1),
(2,  'ABC-456', 'Yamaha',   'FZ 2.0',      2022, 12.00, 'EN_USO',       2),
(3,  'ABC-789', 'Suzuki',   'AX150',       2024, 8.00,  'EN_USO',       3),
(4,  'DEF-123', 'Honda',    'CB190R',      2023, 15.00, 'EN_USO',       4),
(5,  'DEF-456', 'Bajaj',    'Pulsar NS200',2022, 12.00, 'EN_USO',       5),
(6,  'DEF-789', 'Yamaha',   'MT-03',       2024, 10.00, 'EN_USO',       6),
(7,  'GHI-123', 'Honda',    'PCX 160',     2023, 20.00, 'EN_USO',       7),
(8,  'GHI-456', 'Suzuki',   'Gixxer 150',  2022, 10.00, 'EN_USO',       8),
(9,  'GHI-789', 'Kawasaki', 'Z125 Pro',    2021, 8.00,  'EN_MANTENIMIENTO', 9),
(10, 'JKL-123', 'Yamaha',   'NMAX 155',    2024, 15.00, 'EN_USO',       10),
(11, 'JKL-456', 'Honda',    'ADV 160',     2023, 18.00, 'EN_USO',       11),
(12, 'JKL-789', 'Bajaj',    'Dominar 400', 2022, 20.00, 'DISPONIBLE',   12),
(13, 'MNO-123', 'Suzuki',   'Access 125',  2023, 10.00, 'EN_USO',       13),
(14, 'MNO-456', 'Yamaha',   'Aerox 155',   2024, 12.00, 'EN_USO',       14),
(15, 'MNO-789', 'Honda',    'Forza 350',   2023, 25.00, 'EN_USO',       15);

-- ------------------------------------------------------------
-- RUTAS (10 rutas comunes en Lima)
-- ------------------------------------------------------------
INSERT INTO rutas (id, nombre, origen, destino, distancia_km, tiempo_estimado_min, activa) VALUES
(1,  'Centro - Miraflores',     'Cercado de Lima',          'Miraflores',              8.50,  25.00, TRUE),
(2,  'Surco - San Isidro',      'Santiago de Surco',        'San Isidro',              6.20,  18.00, TRUE),
(3,  'San Miguel - Barranco',   'San Miguel',               'Barranco',                5.80,  15.00, TRUE),
(4,  'Los Olivos - Centro',     'Los Olivos',               'Cercado de Lima',         12.30, 35.00, TRUE),
(5,  'Surquillo - Miraflores',  'Surquillo',                'Miraflores',              3.20,  10.00, TRUE),
(6,  'Magdalena - San Borja',   'Magdalena del Mar',        'San Borja',               7.10,  20.00, TRUE),
(7,  'Chorrillos - Centro',     'Chorrillos',               'Cercado de Lima',         15.60, 40.00, TRUE),
(8,  'Santa Anita - Surco',     'Santa Anita',              'Santiago de Surco',       9.40,  28.00, TRUE),
(9,  'Lince - San Isidro',      'Lince',                    'San Isidro',              4.50,  12.00, TRUE),
(10, 'Breña - Magdalena',       'Breña',                    'Magdalena del Mar',       5.00,  14.00, TRUE);

-- ------------------------------------------------------------
-- PEDIDOS (30 pedidos en diversos estados)
-- ------------------------------------------------------------
INSERT INTO pedidos (id, codigo, cliente_id, repartidor_id, ruta_id, direccion_origen, direccion_destino, peso, tipo_paquete, estado, costo, fecha_estimada, fecha_entrega, created_at) VALUES
-- ENTREGADOS (1-8)
(1,  'TRK-2026-001', 1,  1,  1, 'Av. Javier Prado Este 4200', 'Av. Larco 1234, Miraflores',     2.50, 'PAQUETE_MEDIANO', 'ENTREGADO', 35.00, '2026-06-25 14:00:00', '2026-06-25 13:45:00', '2026-06-25 09:00:00'),
(2,  'TRK-2026-002', 2,  2,  2, 'Jr. de la Unión 678',        'Av. Camino Real 300, Surco',     1.20, 'DOCUMENTO',       'ENTREGADO', 22.00, '2026-06-25 16:00:00', '2026-06-25 15:30:00', '2026-06-25 10:00:00'),
(3,  'TRK-2026-003', 3,  3,  3, 'Av. Arequipa 1455',          'Calle San Martín 456, Barranco', 0.80, 'DOCUMENTO',       'ENTREGADO', 18.00, '2026-06-26 12:00:00', '2026-06-26 11:50:00', '2026-06-26 08:30:00'),
(4,  'TRK-2026-004', 4,  4,  4, 'Av. La Marina 234',          'Jr. Carabaya 110, Centro',       5.00, 'PAQUETE_GRANDE',  'ENTREGADO', 55.00, '2026-06-26 18:00:00', '2026-06-26 17:20:00', '2026-06-26 11:00:00'),
(5,  'TRK-2026-005', 5,  5,  5, 'Av. Brasil 2100',            'Av. Petit Thouars 5000, Surquillo', 1.80, 'PAQUETE_PEQUENO', 'ENTREGADO', 25.00, '2026-06-27 10:00:00', '2026-06-27 09:45:00', '2026-06-27 07:00:00'),
(6,  'TRK-2026-006', 6,  6,  6, 'Calle Los Olivos 567',       'Av. Javier Prado Oeste 2800, Magdalena', 3.20, 'PAQUETE_MEDIANO', 'ENTREGADO', 40.00, '2026-06-27 15:00:00', '2026-06-27 14:40:00', '2026-06-27 10:00:00'),
(7,  'TRK-2026-007', 7,  7,  7, 'Av. Angamos 1234',           'Jr. Zavala 500, Centro',         0.50, 'DOCUMENTO',       'ENTREGADO', 15.00, '2026-06-28 11:00:00', '2026-06-28 10:55:00', '2026-06-28 08:00:00'),
(8,  'TRK-2026-008', 8,  8,  8, 'Jr. Tacna 456',              'Av. Angamos 2000, Surco',        4.00, 'PAQUETE_MEDIANO', 'ENTREGADO', 45.00, '2026-06-28 16:00:00', '2026-06-28 15:50:00', '2026-06-28 12:00:00'),

-- EN_TRANSITO (9-14)
(9,  'TRK-2026-009', 9,  1,  1, 'Av. Benavides 890',          'Jr. de la Unión 200, Centro',    2.00, 'PAQUETE_MEDIANO', 'EN_TRANSITO', 30.00, '2026-06-30 14:00:00', NULL, '2026-06-30 09:00:00'),
(10, 'TRK-2026-010', 10, 3,  9, 'Av. Rivera Navarrete 650',   'Av. Paz Soldán 900, San Isidro', 1.50, 'PAQUETE_PEQUENO', 'EN_TRANSITO', 20.00, '2026-06-30 15:00:00', NULL, '2026-06-30 09:30:00'),
(11, 'TRK-2026-011', 11, 6,  3, 'Calle Schell 345',           'Av. La Marina 800, San Miguel',  0.70, 'DOCUMENTO',       'EN_TRANSITO', 16.00, '2026-06-30 12:00:00', NULL, '2026-06-30 10:00:00'),
(12, 'TRK-2026-012', 12, 11, 6, 'Av. Aviación 1800',          'Av. Salaverry 3000, San Borja',  3.80, 'PAQUETE_GRANDE',  'EN_TRANSITO', 50.00, '2026-06-30 17:00:00', NULL, '2026-06-30 11:00:00'),
(13, 'TRK-2026-013', 13, 15, 7, 'Av. Guardia Civil 500',      'Jr. Lampa 600, Centro',          2.20, 'PAQUETE_MEDIANO', 'EN_TRANSITO', 32.00, '2026-06-30 16:00:00', NULL, '2026-06-30 10:30:00'),
(14, 'TRK-2026-014', 14, 10, 8, 'Jr. Carabayllo 123',         'Av. Aviación 2200, Surco',       1.00, 'DOCUMENTO',       'EN_TRANSITO', 19.00, '2026-06-30 13:00:00', NULL, '2026-06-30 08:00:00'),

-- EN_RECOJO (15-18)
(15, 'TRK-2026-015', 1,  4,  2, 'Av. Javier Prado Este 4200', 'Av. Camino Real 1500, Surco',    1.30, 'DOCUMENTO',       'EN_RECOJO', 20.00, '2026-06-30 18:00:00', NULL, '2026-06-30 12:00:00'),
(16, 'TRK-2026-016', 3,  7,  5, 'Av. Arequipa 1455',          'Av. Petit Thouars 3000, Surquillo', 2.80, 'PAQUETE_MEDIANO', 'EN_RECOJO', 35.00, '2026-06-30 19:00:00', NULL, '2026-06-30 13:00:00'),
(17, 'TRK-2026-017', 5,  12, 9, 'Av. Brasil 2100',            'Av. Paz Soldán 500, San Isidro', 0.60, 'DOCUMENTO',       'EN_RECOJO', 15.00, '2026-06-30 20:00:00', NULL, '2026-06-30 14:00:00'),
(18, 'TRK-2026-018', 7,  13, 10,'Av. Angamos 1234',            'Calle Coronel Zegarra 100, Breña', 1.90, 'PAQUETE_PEQUENO', 'EN_RECOJO', 24.00, '2026-06-30 17:30:00', NULL, '2026-06-30 11:30:00'),

-- ASIGNADOS (19-22)
(19, 'TRK-2026-019', 2,  5,  4, 'Jr. de la Unión 678',        'Av. Angamos 800, Surquillo',     3.50, 'PAQUETE_GRANDE',  'ASIGNADO',  48.00, '2026-07-01 12:00:00', NULL, '2026-06-30 15:00:00'),
(20, 'TRK-2026-020', 4,  8,  7, 'Av. La Marina 234',          'Jr. Lampa 300, Centro',          2.00, 'PAQUETE_MEDIANO', 'ASIGNADO',  30.00, '2026-07-01 14:00:00', NULL, '2026-06-30 15:30:00'),
(21, 'TRK-2026-021', 9,  14, 2, 'Av. Benavides 890',          'Av. Camino Real 500, Surco',     1.00, 'DOCUMENTO',       'ASIGNADO',  18.00, '2026-07-01 10:00:00', NULL, '2026-06-30 16:00:00'),
(22, 'TRK-2026-022', 11, 2,  3, 'Calle Schell 345',           'Av. La Marina 1200, San Miguel', 4.50, 'PAQUETE_GRANDE',  'ASIGNADO',  52.00, '2026-07-01 16:00:00', NULL, '2026-06-30 16:30:00'),

-- PENDIENTES (23-28)
(23, 'TRK-2026-023', 6,  NULL, NULL, 'Calle Los Olivos 567',   'Av. Angamos 2500, Surquillo',    0.90, 'DOCUMENTO',       'PENDIENTE', NULL, NULL, NULL, '2026-06-30 17:00:00'),
(24, 'TRK-2026-024', 8,  NULL, NULL, 'Jr. Tacna 456',          'Av. Javier Prado Este 3000, Surco', 2.50, 'PAQUETE_MEDIANO', 'PENDIENTE', NULL, NULL, NULL, '2026-06-30 17:15:00'),
(25, 'TRK-2026-025', 10, NULL, NULL, 'Av. Rivera Navarrete 650', 'Calle Alcanfores 456, Miraflores', 1.70, 'PAQUETE_PEQUENO', 'PENDIENTE', NULL, NULL, NULL, '2026-06-30 17:30:00'),
(26, 'TRK-2026-026', 12, NULL, NULL, 'Av. Aviación 1800',      'Av. Guardia Civil 800, Chorrillos', 3.00, 'PAQUETE_MEDIANO', 'PENDIENTE', NULL, NULL, NULL, '2026-06-30 17:45:00'),
(27, 'TRK-2026-027', 13, NULL, NULL, 'Av. Guardia Civil 500',  'Jr. Carabayllo 300, Santa Anita',  0.40, 'DOCUMENTO',       'PENDIENTE', NULL, NULL, NULL, '2026-06-30 18:00:00'),
(28, 'TRK-2026-028', 14, NULL, NULL, 'Jr. Carabayllo 123',     'Av. Arequipa 2000, Lince',        5.50, 'PAQUETE_GRANDE',  'PENDIENTE', NULL, NULL, NULL, '2026-06-30 18:15:00'),

-- CANCELADOS (29-30)
(29, 'TRK-2026-029', 9,  NULL, NULL, 'Av. Benavides 890',      'Av. La Marina 500, San Miguel',   1.20, 'DOCUMENTO',       'CANCELADO', NULL, NULL, NULL, '2026-06-29 14:00:00'),
(30, 'TRK-2026-030', 6,  NULL, NULL, 'Calle Los Olivos 567',   'Jr. de la Unión 100, Centro',     2.80, 'PAQUETE_MEDIANO', 'CANCELADO', NULL, NULL, NULL, '2026-06-29 16:00:00');

-- ------------------------------------------------------------
-- ENTREGAS (para los pedidos ENTREGADOS y EN_TRANSITO)
-- ------------------------------------------------------------
INSERT INTO entregas (id, pedido_id, repartidor_id, fecha_recojo, fecha_entrega, firma, foto, estado) VALUES
(1,  1,  1,  '2026-06-25 10:00:00', '2026-06-25 13:45:00', 'M. Gutierrez',  'foto_001.jpg', 'ENTREGADO'),
(2,  2,  2,  '2026-06-25 11:00:00', '2026-06-25 15:30:00', 'A. Torres',     'foto_002.jpg', 'ENTREGADO'),
(3,  3,  3,  '2026-06-26 09:00:00', '2026-06-26 11:50:00', 'R. Martinez',   'foto_003.jpg', 'ENTREGADO'),
(4,  4,  4,  '2026-06-26 12:00:00', '2026-06-26 17:20:00', 'L. Fernandez',  'foto_004.jpg', 'ENTREGADO'),
(5,  5,  5,  '2026-06-27 08:00:00', '2026-06-27 09:45:00', 'C. Lopez',      'foto_005.jpg', 'ENTREGADO'),
(6,  6,  6,  '2026-06-27 11:00:00', '2026-06-27 14:40:00', 'P. Ramos',      'foto_006.jpg', 'ENTREGADO'),
(7,  7,  7,  '2026-06-28 09:00:00', '2026-06-28 10:55:00', 'C. Morales',    'foto_007.jpg', 'ENTREGADO'),
(8,  8,  8,  '2026-06-28 13:00:00', '2026-06-28 15:50:00', 'S. Castillo',   'foto_008.jpg', 'ENTREGADO'),
(9,  9,  1,  '2026-06-30 10:00:00', NULL, NULL, NULL, 'EN_CAMINO'),
(10, 10, 3,  '2026-06-30 10:30:00', NULL, NULL, NULL, 'EN_CAMINO'),
(11, 11, 6,  '2026-06-30 10:45:00', NULL, NULL, NULL, 'EN_CAMINO'),
(12, 12, 11, '2026-06-30 11:30:00', NULL, NULL, NULL, 'EN_CAMINO'),
(13, 13, 15, '2026-06-30 11:15:00', NULL, NULL, NULL, 'EN_CAMINO'),
(14, 14, 10, '2026-06-30 09:00:00', NULL, NULL, NULL, 'EN_CAMINO'),
(15, 15, 4,  '2026-06-30 13:00:00', NULL, NULL, NULL, 'PENDIENTE'),
(16, 16, 7,  '2026-06-30 14:00:00', NULL, NULL, NULL, 'PENDIENTE'),
(17, 17, 12, '2026-06-30 15:00:00', NULL, NULL, NULL, 'PENDIENTE'),
(18, 18, 13, '2026-06-30 12:30:00', NULL, NULL, NULL, 'PENDIENTE');

-- ------------------------------------------------------------
-- INCIDENTES (5 incidentes de ejemplo)
-- ------------------------------------------------------------
INSERT INTO incidentes (pedido_id, tipo, descripcion, created_at) VALUES
(9,  'RETRASO',                'Trafico intenso en la Via Expresa, retardo de 15 minutos', '2026-06-30 10:30:00'),
(12, 'DANO',                   'Paquete recibio golpe menor durante el transporte',        '2026-06-30 12:00:00'),
(29, 'CLIENTE_AUSENTE',        'Cliente no se encontraba en la direccion indicada',        '2026-06-29 15:00:00'),
(30, 'DIRECCION_INCORRECTA',   'Direccion proporcionada no existe, cliente cancelo',        '2026-06-29 17:00:00'),
(4,  'RETRASO',                'Lluvia fuerte dificulto el desplazamiento',                '2026-06-26 15:00:00');

-- ------------------------------------------------------------
-- PROMOCIONES
-- ------------------------------------------------------------
INSERT IGNORE INTO promociones (titulo, descripcion, descuento_porcentaje, monto_minimo, fecha_inicio, fecha_fin, activa) VALUES
('Promo de bienvenida', 'Descuento del 15% para envios que superen los S/100', 15.00, 100.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', TRUE),
('Fiestas patrias', 'Descuento del 10% por fiestas patrias', 10.00, NULL, '2026-07-01 00:00:00', '2026-07-31 23:59:59', TRUE),
('Envio gratis', 'Envio gratis en pedidos mayores a S/200', 100.00, 200.00, '2026-06-01 00:00:00', '2026-06-30 23:59:59', FALSE),
('Black Friday', 'Descuento del 25% en todos los envios', 25.00, NULL, '2026-11-25 00:00:00', '2026-11-30 23:59:59', TRUE);

SET FOREIGN_KEY_CHECKS = 1;
