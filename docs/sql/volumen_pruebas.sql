-- Datos de volumen y fixtures exclusivos de pruebas. NO ejecutar en producción.
-- Precondición: Flyway V4 aplicada y esquema PharmaSoft de desarrollo disponible.
-- Ejecutar una sola vez en el esquema de pruebas para crear 5.000 productos y 20.000 ventas.

-- Garantizar categoría para los productos E2E.
insert into categorias (nombre, descripcion, estado, fecha_creacion, creado_por)
select 'Categoria E2E', 'Categoría técnica para pruebas automatizadas', 1, systimestamp, 'pruebas'
from dual
where not exists (select 1 from categorias where nombre = 'Categoria E2E');

-- Cliente determinista que usa Playwright. El DNI es un dato ficticio de laboratorio.
insert into clientes
(dni, nombres, apellidos, email, telefono, direccion, estado, fecha_creacion, creado_por)
select '99999998', 'Cliente E2E', 'Cauna', 'cliente.e2e@pharmasoft.local',
       '900000098', 'Registro de prueba', 1, systimestamp, 'pruebas'
from dual
where not exists (select 1 from clientes where dni = '99999998');

-- Producto determinista para el flujo de venta repetido tres veces.
insert into productos
(nombre, precio, stock, estado, categoria_id, fecha_creacion, creado_por)
select 'A - Paracetamol E2E', 2.50, 1000, 1,
       (select min(id) from categorias where nombre = 'Categoria E2E'),
       systimestamp, 'pruebas'
from dual
where not exists (select 1 from productos where nombre = 'A - Paracetamol E2E');

-- Completar 5.000 productos en total, contando el producto E2E.
merge into productos p
using (
    select 'Producto de prueba ' || to_char(level, 'FM00000') nombre,
           round(dbms_random.value(1, 200), 2) precio,
           trunc(dbms_random.value(0, 500)) stock,
           (select min(id) from categorias) categoria_id
    from dual
    connect by level <= 4999
) src
on (p.nombre = src.nombre)
when not matched then insert
    (nombre, precio, stock, estado, categoria_id, fecha_creacion, creado_por)
values
    (src.nombre, src.precio, src.stock, 1, src.categoria_id, systimestamp, 'carga');

-- Crear 20.000 cabeceras de venta para pruebas de filtros y paginación.
-- Se usa el cliente E2E; son ventas sintéticas sin detalles de venta.
insert into ventas (fecha_registro, total, estado, cliente_id, creado_por)
select
    systimestamp - numtodsinterval(dbms_random.value(0, 365), 'DAY'),
    round(dbms_random.value(5, 500), 2),
    'REGISTRADA',
    (select id from clientes where dni = '99999998'),
    'carga'
from dual
connect by level <= 20000;

commit;

-- Verificación esperada tras una primera ejecución:
select count(*) as total_productos from productos;
select count(*) as total_ventas from ventas;
select id, nombre, stock from productos where nombre = 'A - Paracetamol E2E';
select id, nombres, apellidos from clientes where dni = '99999998';
