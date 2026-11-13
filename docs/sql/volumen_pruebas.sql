-- Carga de volumen exclusiva para desarrollo/pruebas.
-- Ejecutar DESPUÉS de aplicar V4__auditoria.sql y con al menos una categoría
-- y un cliente existentes. Nunca ejecutar en producción.
-- La columna de fecha de Venta en este repositorio se llama FECHA_REGISTRO.

insert into productos (
    nombre, precio, stock, estado, categoria_id, fecha_creacion, creado_por
)
select
    'Producto de prueba ' || lpad(level, 5, '0'),
    round(dbms_random.value(1, 200), 2),
    trunc(dbms_random.value(0, 500)),
    1,
    (select min(id) from categorias),
    systimestamp,
    'carga'
from dual
connect by level <= 5000;

insert into ventas (
    fecha_registro, total, estado, cliente_id, creado_por
)
select
    systimestamp - numtodsinterval(dbms_random.value(0, 365), 'DAY'),
    round(dbms_random.value(5, 500), 2),
    'REGISTRADA',
    (select min(id) from clientes),
    'carga'
from dual
connect by level <= 20000;

commit;

-- Verificación:
select count(*) as total_productos from productos;
select count(*) as total_ventas from ventas;
