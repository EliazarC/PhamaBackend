-- Seguridad Sesion 10.
-- Las tablas ya existen: insertar solo los datos que falten.

-- ROLES
INSERT INTO roles (nombre, descripcion)
SELECT 'ADMIN',
       'Administra usuarios, ve reportes y opera todo el sistema'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE UPPER(nombre) = 'ADMIN'
);

INSERT INTO roles (nombre, descripcion)
SELECT 'VENDEDOR',
       'Atiende clientes y registra ventas'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE UPPER(nombre) = 'VENDEDOR'
);

INSERT INTO roles (nombre, descripcion)
SELECT 'ALMACENERO',
       'Mantiene categorias y productos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE UPPER(nombre) = 'ALMACENERO'
);

-- EMPLEADOS
INSERT INTO empleados
(dni, nombres, apellidos, cargo, email, telefono, estado)
SELECT
    '40112233', 'Rosa Elena', 'Mendoza Ticona',
    'Administradora', 'rmendoza@pharmasoft.pe', '951000111', TRUE
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM empleados WHERE dni = '40112233'
);

INSERT INTO empleados
(dni, nombres, apellidos, cargo, email, telefono, estado)
SELECT
    '41223344', 'Lucia', 'Ramos Cutipa',
    'Vendedora', 'lramos@pharmasoft.pe', '951000222', TRUE
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM empleados WHERE dni = '41223344'
);

INSERT INTO empleados
(dni, nombres, apellidos, cargo, email, telefono, estado)
SELECT
    '42334455', 'Pedro', 'Quispe Mamani',
    'Almacenero', 'pquispe@pharmasoft.pe', NULL, TRUE
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM empleados WHERE dni = '42334455'
);

INSERT INTO empleados
(dni, nombres, apellidos, cargo, email, telefono, estado)
SELECT
    '43445566', 'Ana', 'Torres Vilca',
    'Jefa de tienda', 'atorres@pharmasoft.pe', '951000444', TRUE
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM empleados WHERE dni = '43445566'
);

INSERT INTO empleados
(dni, nombres, apellidos, cargo, email, telefono, estado)
SELECT
    '44556677', 'Jorge', 'Apaza Huanca',
    'Vendedor', 'japaza@pharmasoft.pe', NULL, TRUE
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM empleados WHERE dni = '44556677'
);

INSERT INTO empleados
(dni, nombres, apellidos, cargo, email, telefono, estado)
SELECT
    '45667788', 'Maria', 'Huaman Torres',
    'Cajera', 'mhuaman@pharmasoft.pe', NULL, TRUE
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM empleados WHERE dni = '45667788'
);

-- USUARIOS
-- EMAIL y FECHA_CREACION son obligatorios en la tabla existente.

INSERT INTO usuarios
(username, password, estado, empleado_id, email, fecha_creacion)
SELECT
    'admin',
    '$2a$10$NNNgOCCajIjVDGhg3u7sHOwLlLrVMdIALH/64AmPlFmmpiG1yinGa',
    TRUE, e.id, e.email, CURRENT_TIMESTAMP
FROM empleados e
WHERE e.dni = '40112233'
  AND NOT EXISTS (
    SELECT 1 FROM usuarios u
    WHERE UPPER(u.username) = 'ADMIN'
);

INSERT INTO usuarios
(username, password, estado, empleado_id, email, fecha_creacion)
SELECT
    'vendedor',
    '$2a$10$EXmYQXoQUBed/ZQHfPVA1.eTtSKOdgQpsz3/ZODq.za2hyGfV40We',
    TRUE, e.id, e.email, CURRENT_TIMESTAMP
FROM empleados e
WHERE e.dni = '41223344'
  AND NOT EXISTS (
    SELECT 1 FROM usuarios u
    WHERE UPPER(u.username) = 'VENDEDOR'
);

INSERT INTO usuarios
(username, password, estado, empleado_id, email, fecha_creacion)
SELECT
    'almacen',
    '$2a$10$ciG8Sz1SQTaIK5Cau0G/5u1LqIPQvcujkc5hPRaGO/Fd7ChrFBkFK',
    TRUE, e.id, e.email, CURRENT_TIMESTAMP
FROM empleados e
WHERE e.dni = '42334455'
  AND NOT EXISTS (
    SELECT 1 FROM usuarios u
    WHERE UPPER(u.username) = 'ALMACEN'
);

INSERT INTO usuarios
(username, password, estado, empleado_id, email, fecha_creacion)
SELECT
    'supervisor',
    '$2a$10$j8xXh/XBHa/3jkViqNkMs.oZtvG6kL/QyF49kDAnnDrqktpT75sJK',
    TRUE, e.id, e.email, CURRENT_TIMESTAMP
FROM empleados e
WHERE e.dni = '43445566'
  AND NOT EXISTS (
    SELECT 1 FROM usuarios u
    WHERE UPPER(u.username) = 'SUPERVISOR'
);

INSERT INTO usuarios
(username, password, estado, empleado_id, email, fecha_creacion)
SELECT
    'inactivo',
    '$2a$10$oi118hH.Nfj8jY0/MOIWJ.i34FhMDm2jy0/OpPMjSMUPh1.W/C6De',
    FALSE, e.id, e.email, CURRENT_TIMESTAMP
FROM empleados e
WHERE e.dni = '44556677'
  AND NOT EXISTS (
    SELECT 1 FROM usuarios u
    WHERE UPPER(u.username) = 'INACTIVO'
);

-- USUARIO_ROLES
-- Agregar solamente las asignaciones que falten.

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
         CROSS JOIN roles r
WHERE UPPER(u.username) = 'ADMIN'
  AND UPPER(r.nombre) = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur
    WHERE ur.usuario_id = u.id
      AND ur.rol_id = r.id
);

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
         CROSS JOIN roles r
WHERE UPPER(u.username) = 'VENDEDOR'
  AND UPPER(r.nombre) = 'VENDEDOR'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur
    WHERE ur.usuario_id = u.id
      AND ur.rol_id = r.id
);

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
         CROSS JOIN roles r
WHERE UPPER(u.username) = 'ALMACEN'
  AND UPPER(r.nombre) = 'ALMACENERO'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur
    WHERE ur.usuario_id = u.id
      AND ur.rol_id = r.id
);

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
         CROSS JOIN roles r
WHERE UPPER(u.username) = 'SUPERVISOR'
  AND UPPER(r.nombre) IN ('VENDEDOR', 'ALMACENERO')
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur
    WHERE ur.usuario_id = u.id
      AND ur.rol_id = r.id
);

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
         CROSS JOIN roles r
WHERE UPPER(u.username) = 'INACTIVO'
  AND UPPER(r.nombre) = 'VENDEDOR'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur
    WHERE ur.usuario_id = u.id
      AND ur.rol_id = r.id
);