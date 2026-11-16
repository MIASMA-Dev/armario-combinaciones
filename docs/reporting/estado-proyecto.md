# Estado del proyecto — Armario y Combinaciones

Actualizado: 2026-11-16

## Versiones

| Version | Alcance | Estado |
|---|---|---|
| v0.1 | Base backend/frontend + libreria 11 + diseño Pop Culture | Completada |
| v0.2 | Persistencia, usuarios, perfiles, auth + Google | Completada (base) |
| v0.3 | Combinaciones + captura por foto | Completada |
| v0.4 | Multiplataforma (Tauri/Capacitor) + sync | Completada (build iOS requiere macOS) |
| v0.5 | Diseño y assets | Completada |
| v0.6 | Gestión funcional (prendas/perfiles/órdenes) | Completada (8.1–8.6 cerradas; cierre funcional del producto listo, release v1.0 pendiente) |
| v1.0 | Release | Pendiente (QA cerrada; faltan shells y assets reales) |

## Iteraciones

| Iteracion | Tareas | Estado |
|---|---|---|
| 1. Persistencia | JPA, User, Profile, repos, datasource | ✅ Completada |
| 2. Auth y perfiles | register, login, Google, perfiles, compartir | ✅ Completada |
| 3. Prendas por perfil | Prenda + profileId, endpoints, UI | ✅ Completada |
| 3b. Captura por foto | Vision analyze (mock) + UI | ✅ Completada (mock) |
| 4. Combinaciones | TipoPrenda, generador, endpoint, UI | ✅ Completada |
| 5. Diseño Pop Culture | Estilos, responsive, assets | ✅ Completada (assets integrados) |
| 6. Multiplataforma | Tauri, Capacitor, sync | ✅ Completada (build iOS requiere macOS) |
| 7. QA | Tests backend/frontend, verificacion | ✅ Completada (runtime Tauri/Capacitor pendiente) |
| 8. Gestión funcional | Prendas/perfiles UI, órdenes, PostgreSQL | ⏳ En curso (8.1, 8.2, 8.3, 8.4, 8.5 y 8.6 completadas; cierre v1.0 pendiente) |

## Notas

- Backend compila con JDK 25 usando dependencias en cache; `mvn test` funciona con Maven local en `.build/maven`.
- Backend verificado en ejecucion: login, token Bearer, perfiles, combinaciones, 403 y logout.
- Frontend: `npm install` y `npm run build` verificados (Node 26 / npm 12).
- Tests unitarios: 6/6 en `CombinacionServiceTest` (JUnit 6).
- QA (iteracion 7): 16 tests backend (`ApiIntegrationTest` 7, `CombinacionServiceTest` 6, `VisionControllerTest` 3) y 8 frontend (`CacheService`, `CombinacionService`, `AuthService`) en verde.
- Sesion persistente: `GET /api/auth/me` + `AuthService.restore()` al recargar. Seguridad responde 401 (no redireccion) sin token.
- Corregidos en QA: desbordamiento horizontal (`box-sizing: border-box`), plural de contadores, color dominante cuantizado (media real del bucket) y restauracion de sesion.
- Iteracion 8.1: `PUT /api/prendas/{id}` (actualizacion parcial con control de acceso) y UI de inventario con cambio de estado, eliminar con confirmacion y filtros reales por temporada/color/estado.
- Iteracion 8.2: UI de perfiles (crear y compartir por email, solo propietario) con actualizacion de cache; verificado que Carlos ve el perfil compartido.
- Iteracion 8.3: `ultimoUso` en `Prenda`, `GET /api/ordenes/generar` con inactividad >180 dias, prioridad por antiguedad y reposicion cuando hay <2 prendas disponibles por categoria; pestana Ordenes en UI con filtros propios, "Marcar en uso" y "Capturar prenda" (con categoria precargada). Las combinaciones ahora ignoran prendas no disponibles.
- Iteracion 8.4 (en curso): perfil de Spring `postgres` (`application-postgres.properties`) y arranque con `mvn spring-boot:run -Dspring-boot.run.profiles=dev,postgres`; scripts `scripts/postgres-up.ps1` / `postgres-down.ps1` y `docker/podman-compose.yml` con servicio `postgres:16-alpine` (usuario/db `armario`). Pendiente: arrancar PostgreSQL con Podman, arrancar backend con perfil postgres y verificar datos persisten tras reinicio; tests siguen en H2.
- Iteracion 8.4 (completada): PostgreSQL levantado con Podman (`armario-postgres`), backend arrancado con perfiles `dev,postgres` (dialecto `PostgreSQLDialect`, DB `jdbc:postgresql://localhost:5432/armario`), prenda creada y verificada tras reinicio (persistencia OK); tests con H2 en verde.
- Iteracion 8.5 (completada): limite de usuarios por instalacion (`armario.max-users=3` en `application.properties`); `POST /api/auth/register` y alta de Google devuelven 403 con error "limite de usuarios alcanzado" al superarlo; `ApiIntegrationTest` usa max-users=50; nuevo `AuthLimitTest` verifica 3 registros OK y el 4to en 403.
- Iteracion 8.6 (completada con aclaracion): solo se guarda la descripcion de la captura (color/tipo/tejido); no se persiste la imagen en el dispositivo. `Prenda.tejido` + `PrendaRequest.tejido` + envio desde la vision en captura; test `capturaGuardaDescripcionDePrendaSinGuardarImagen`.
- Seed de usuarios test: ana@armario.test, carlos@armario.test, abuela@armario.test (password: test1234).
- Perfiles compartidos: Peque (NINO) compartido Ana/Carlos; Bebe (BEBE) compartido Carlos/Ana.
- Google Sign-In: backend validaba `aud` del ID token contra `armario.google.client-id` y frontend leia client id en runtime (`armario.googleClientId`); hoy el login con Google quedo eliminado (frontend: boton/GSI/`loginWithGoogle`; backend: `POST /api/auth/google`) para la release Android-only. Solo email/password queda en la app.
- Combinaciones: `GET /api/combinaciones?profileId=` genera outfits por categoria y armonia de color (neutro, monocromatico, analogo, complementario); maximo 24 resultados.
- Seed de prendas de ejemplo en el perfil "Ana" (MUJER) para probar combinaciones.
- Multiplataforma: Tauri v2 (`frontend/src-tauri`), Capacitor Android (`frontend/android`) e iOS (`frontend/ios`). La API se resuelve con `API_BASE` (navegador relativo; Tauri/iOS `localhost:8080`; emulador Android `10.0.2.2:8080`).
- Auth nativa: token Bearer en `localStorage` + sesion por cookie en web. Endpoint `POST /api/auth/logout`.
- Cache offline: `localStorage` por perfil (`prendas`, `combinaciones`, `ordenes`, `profiles`) con aviso "Sin conexion" en la UI.
- Podman dev: `scripts/podman-up.ps1 -Build`.