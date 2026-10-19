# PharmaBackend — Seguridad Sesión 10

## Configuración
- Perfil de desarrollo: `dev`, puerto `8081`, Oracle `FREEPDB1`. La migración `V2__seguridad.sql` crea tablas y cuentas de demostración; no modificar migraciones ya aplicadas.
- En desarrollo puede usarse la clave de prueba de `application-dev.yaml`. En producción definir `JWT_SECRET` como clave Base64 aleatoria de al menos 32 bytes y `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- Para iniciar: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). Swagger: `http://localhost:8081/swagger-ui.html`.

## Cuentas de demostración
`admin / Admin2026*`, `vendedor / Vendedor2026*`, `almacen / Almacen2026*`, `supervisor / Supervisor2026*`. Cuenta inactiva: `inactivo / Inactivo2026*` (debe ser rechazada). Estas cuentas son solo para laboratorio.

## Rutas principales
- `POST /api/v1/auth/login` — login y token JWT.
- `GET /api/v1/auth/me` — datos de la cuenta autenticada.
- `PUT /api/v1/auth/password` — cuerpo `{"passwordActual":"...","passwordNueva":"..."}`.
- `GET /api/v1/empleados`, `GET /api/v1/roles`, `GET/POST /api/v1/usuarios` — solo administración.
- El resto de la API requiere `Authorization: Bearer <token>` y permisos según el rol.

## Pruebas
`./mvnw test -Dtest=JwtServiceTest,PermisosPorRolTest`.
