# Sesión 14 — Integración y estabilización

## Implementación incluida en esta rama

### Backend
- Migración Flyway V4: columnas de usuario creador/modificador, tabla de auditoría e índices.
- Auditoría JPA mediante `AuditorAware`.
- Bitácora de eventos confirmados mediante `@TransactionalEventListener(AFTER_COMMIT)`.
- Auditoría de login exitoso/fallido en transacción independiente.
- Endpoint paginado `GET /api/v1/auditoria`, restringido a `USUARIOS_ADMINISTRAR`.
- Paginación de ventas con filtros por cliente, estado y fechas.
- Tamaño de página máximo de 100 para productos.

### Frontend
- Paginador compartido en Productos, Ventas y Auditoría.
- Estado de página/tamaño/orden sincronizado con query params.
- Atributos `data-testid` para flujo de nueva venta.
- Suite E2E Playwright con siete pruebas.

## Pasos locales pendientes
1. Confirmar que Flyway V1–V3 aparecen aplicadas en `flyway_schema_history` antes de arrancar esta rama.
2. Arrancar Oracle y Redis; aplicar V4 con la aplicación.
3. Ejecutar `docs/sql/volumen_pruebas.sql` solo en un esquema de desarrollo/pruebas.
4. Verificar que el cliente y producto de datos E2E existan y que el producto tenga stock suficiente.
5. Instalar Playwright: `npm install` y `npx playwright install chromium`.
6. Ejecutar `npm run build`, `npx playwright test` y después `npx playwright test --repeat-each=3`.
7. Completar `docs/incidencias.md` y el informe PDF con mediciones y capturas reales.

No se deben registrar como aprobadas pruebas que aún no se han ejecutado en el entorno local.
