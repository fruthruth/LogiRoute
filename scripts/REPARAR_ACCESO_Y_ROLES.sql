-- Ejecutar sobre la base de datos que usa LogiRoute.
-- Contraseña de demostración resultante: 123456

UPDATE usuarios
SET activo = TRUE,
    password = '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS'
WHERE email = 'admin@logiroute.com';

UPDATE usuarios u
JOIN repartidores r ON r.usuario_id = u.id
SET u.rol = 'REPARTIDOR',
    u.activo = TRUE,
    u.password = '$2a$10$Keg30UKRXQjNuJ59CSTmQOtRXQO6Bvr3qpEdeUcsh3wes/7FWVwBS';

-- Verificación
SELECT u.id, u.nombre, u.email, u.rol, u.activo, r.id AS repartidor_id
FROM usuarios u
LEFT JOIN repartidores r ON r.usuario_id = u.id
WHERE u.rol IN ('ADMINISTRADOR', 'REPARTIDOR')
ORDER BY u.id;
