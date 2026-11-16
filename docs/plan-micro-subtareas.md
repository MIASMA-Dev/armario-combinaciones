# Plan de micro-subtareas (iterativo)

> Una subtarea = una acción pequeña. No avanzar sin completar la anterior. Marcar [x] al terminar.

## Iteracion 1 — Persistencia (v0.2) ✅
- [x] 1.1 Añadir dependencia JPA (spring-boot-starter-data-jpa) y driver H2/PostgreSQL.
- [x] 1.2 Crear entidad JPA User (tabla users).
- [x] 1.3 Crear entidad JPA Profile (tabla profiles) con sharedWith.
- [x] 1.4 Crear repositorios JPA (UserRepository, ProfileRepository).
- [x] 1.5 Configurar application.properties (datasource).

## Iteracion 2 — Auth y perfiles (v0.2)
- [x] 2.1 Endpoint POST /api/auth/register.
- [x] 2.2 Endpoint POST /api/auth/login (BCrypt + Spring Security).
- [x] 2.2b Endpoint POST /api/auth/google (verificacion de ID token de Google). [Sustituido por 8.8: se quito el login con Google para la release Android-only.]
- [x] 2.3 Endpoint GET /api/profiles (del usuario autenticado).
- [x] 2.4 Endpoint POST /api/profiles (crear perfil).
- [x] 2.5 Compartir perfil: POST /api/profiles/{id}/share.

## Iteracion 3 — Prendas por perfil (v0.2/v0.3) ✅
- [x] 3.1 Crear entidad Prenda con profileId y atributos (color, temporada, talla, tipoPrenda, estado).
- [x] 3.2 Crear PrendaRepository JPA.
- [x] 3.3 Endpoints GET/POST/DELETE /api/prendas?profileId=...
- [x] 3.4 Frontend: selector de perfil y listado de prendas.

## Iteracion 3b — Captura por foto (opcional, v0.3) ✅ (mock)
- [x] 3b.1 Endpoint POST /api/vision/analyze (multipart) que devuelve color, tipoPrenda, tejido, confianza.
- [x] 3b.2 Mock de vision: color dominante por cuantizacion + tipo/tejido de ejemplo.
- [x] 3b.3 Frontend: captura de foto y formulario de confirmacion/correccion.

## Iteracion 4 — Combinaciones (v0.3) ✅
- [x] 4.1 Crear enum TipoPrenda (SUPERIOR, INFERIOR, ROPA_INTERIOR, CALCETINES, ZAPATOS, COMPLEMENTOS, JOYAS, SOMBREROS, CHAQUETAS, ABRIGOS).
- [x] 4.2 Crear modelo Combinacion (id, profileId, prendas, esquemaColor) — DTO CombinacionDTO.
- [x] 4.3 CombinacionService (por color y categoria de prenda, con armonias de color HSB).
- [x] 4.4 Endpoint GET /api/combinaciones?profileId=...
- [x] 4.5 Frontend: pantalla de combinaciones (pestana + grid de outfits + filtros por categoría y color).
- [x] 4.6 Seed de prendas de ejemplo para probar combinaciones.

## Iteracion 5 — Diseño Pop Culture (v0.5)
- [x] 5.1 Aplicar estilos Pop Culture al frontend (ya iniciado).
- [x] 5.2 Responsive mobile/desktop (media query <=640px).
- [x] 5.3 Integrar assets generados (carpeta `public/assets/armario`, hero de login, textura, placeholder de prenda).

## Iteracion 6 — Multiplataforma (v0.4)
- [x] 6.1 Configurar Tauri (src-tauri, ventana 1100x760, devUrl Angular).
- [x] 6.2 Configurar Capacitor (config + plataformas Android e iOS; compilar iOS requiere macOS).
- [x] 6.3 Sync basica: API_BASE nativa, token Bearer, cache local por perfil y aviso offline.

## Iteracion 7 — QA (v1.0)
- [x] 7.1 Tests backend: `ApiIntegrationTest` (7), `CombinacionServiceTest` (6) y `VisionControllerTest` (3).
- [x] 7.2 Tests frontend: `CacheService`, `CombinacionService` y `AuthService` (8 en total).
- [x] 7.3 Verificacion en navegador: login, sesion persistente (`/api/auth/me`), combinaciones y filtros, offline con cache, captura con vision y alta de prenda. Pendiente runtime Tauri/Capacitor y build iOS (macOS).

## Iteracion 8 — Gestion y cierre funcional (v0.6)
- [x] 8.1 Gestion de prendas en UI: cambiar estado, eliminar con confirmacion y filtros reales de inventario (temporada/color/estado).
- [x] 8.2 Gestion de perfiles en UI: crear perfil y compartir por email (solo propietario); cache de perfiles actualizada.
- [x] 8.3 Ordenes: ultimo uso, sugerencias por inactividad (>180 dias) y reposicion por stock bajo; pestana UI con "Marcar en uso" y "Capturar prenda".
- [x] 8.4 Persistencia PostgreSQL para desarrollo (perfil `postgres` + Podman). Completado: `application-postgres.properties`, scripts `postgres-up/down.ps1`, compose con `postgres:16-alpine`; backend arrancado con `dev,postgres` y verificado (conexión PostgreSQLDialect, prenda persistió tras reinicio); tests en H2.
- [x] 8.5 Limite de 3 usuarios por instalacion. (AuthController: 403 en register y alta de Google al superar `armario.max-users=3`; ApiIntegrationTest con max-users=50; nuevo AuthLimitTest con limite 3: 3 registros OK + 4to en 403.)
- [x] 8.6 Guardar imagen de la prenda capturada. Aclaracion del usuario: solo se guarda la descripcion (color/tipo/tejido); no se persiste la imagen en el dispositivo. Implementado: Prenda.tejido + PrendaRequest.tejido + envio desde vision (`tejido`) en captura.
- [x] 8.7 Google Sign-In con client id configurable: backend valida `aud` del ID token contra `armario.google.client-id`; frontend lee `armario.googleClientId` (localStorage) con fallback. Pendiente credencial real (`GOOGLE_CLIENT_ID`) y flujo GSI real (T-AUTH-05/06).
- [x] 8.8 Quitar login con Google para release Android-only: frontend (boton GSI, script GSI, `loginWithGoogle`) y backend (`POST /api/auth/google`) eliminados; solo email/password queda en la app.