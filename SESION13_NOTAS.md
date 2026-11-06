# Sesión 13 — PharmaBackend

## Cambios incluidos
- Dependencias de Spring Cache, Spring Data Redis y Actuator.
- Redis configurable mediante REDIS_HOST y REDIS_PORT; TTL de 10 minutos.
- CacheConfig registra advertencias si Redis falla para permitir que la operación continúe contra Oracle.
- RequestIdFilter genera X-Request-Id y agrega requestId al MDC.
- Los DTO almacenados en caché deben implementar Serializable.

## Pendiente de validación local
- Ejecutar Redis 7 y comprobar PONG.
- Ejecutar ./mvnw clean test.
- Probar dos GET de categorías y confirmar que el segundo no genera SQL.
- Probar invalidación tras crear/actualizar/eliminar categoría/producto y registrar venta.
- Verificar /actuator/health y /actuator/metrics con roles reales.
- Completar las mediciones y capturas reales. No se inventan resultados.
