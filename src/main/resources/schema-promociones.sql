-- ============================================================
-- TABLA: promociones
-- ============================================================
CREATE TABLE promociones (
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
-- DATOS INICIALES: Promociones de ejemplo
-- ============================================================
INSERT INTO promociones (titulo, descripcion, descuento_porcentaje, monto_minimo, fecha_inicio, fecha_fin, activa)
VALUES ('Promo de bienvenida', 'Descuento del 15% para envíos que superen los S/100', 15.00, 100.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', TRUE);

INSERT INTO promociones (titulo, descripcion, descuento_porcentaje, monto_minimo, fecha_inicio, fecha_fin, activa)
VALUES ('Fiestas patrias', 'Descuento del 10% por fiestas patrias', 10.00, NULL, '2026-07-01 00:00:00', '2026-07-31 23:59:59', TRUE);
