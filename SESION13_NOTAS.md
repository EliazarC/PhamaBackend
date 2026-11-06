# Sesión 13 — PharmaBackend

## Cambios incluidos
- Dependencias de Spring Cache, Spring Data Redis y Actuator.
- Redis configurable mediante REDIS_HOST y REDIS_PORT; TTL de 10 minutos.
- CacheConfig registra advertencias si Redis falla para permitir que la operación continúe contra Oracle.
- Caché e invalidación de categorías y productos.
- Caché de productos por página, tamaño, orden y dirección; la API devuelve PaginaResponseDTO.
- La caché de productos se invalida al crear, actualizar o eliminar productos, al modificar/eliminar categorías y al registrar ventas.
- DTO de categorías, productos y páginas serializables para almacenarse en Redis.
- RequestIdFilter genera X-Request-Id y agrega requestId al MDC.
- Actuator expone health, info y metrics; health es público y el resto requiere USUARIOS_ADMINISTRAR.
- Los logs de producción se guardan en logs/pharma-backend.log con rotación.
- Las credenciales Oracle y la clave JWT se leen desde variables de entorno también en desarrollo; no se guardan en archivos versionados.

## Preparación local en Windows PowerShell

En la terminal del Backend, establece la contraseña de tu usuario Oracle y genera una clave JWT Base64 aleatoria de 256 bits para esta sesión de terminal:

```powershell
$env:DB_PASSWORD = "TU_CLAVE_ORACLE"
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Inicia Redis si todavía no existe el contenedor:

```powershell
docker run -d --name pharma-redis -p 6379:6379 redis:7-alpine
docker exec -it pharma-redis redis-cli ping
```

El resultado de Redis debe ser `PONG`. Si el contenedor ya existe, usa `docker start pharma-redis` en lugar de crearlo otra vez.

Compila y ejecuta pruebas:

```powershell
.\mvnw.cmd clean test
```

Para iniciar en desarrollo:

```powershell
.\mvnw.cmd spring-boot:run
```

## Validación pendiente con servicios reales
- Confirmar que Oracle conecta con `jdbc:oracle:thin:@//localhost:1522/FREEPDB1`.
- Ejecutar dos GET /api/v1/categorias seguidos y confirmar que el segundo no genera SQL.
- Ejecutar dos GET /api/v1/productos con los mismos parámetros y verificar el hit de caché.
- Probar que las escrituras invalidan las cachés correspondientes.
- Apagar Redis, abrir Categorías y comprobar continuidad de Oracle, WARN y estado DOWN en /actuator/health.
- Revisar X-Request-Id y que el mismo identificador aparezca en el log.
- Comprobar /actuator/health sin token, /actuator/metrics como admin y como vendedor.
- Repetir con perfil prod y variables DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET y FRONTEND_ORIGINS.
- Guardar capturas reales y registrar resultados en la tabla de evidencias antes/después. No se inventan tamaños, tiempos ni resultados.
