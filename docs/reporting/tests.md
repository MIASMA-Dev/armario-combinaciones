# Reporting de tests — Armario y Combinaciones

Actualizado: 2026-11-16

## Entorno

- Frontend: Angular 22; `npm install` + `ng build` verificados (Node v26.12.1 / npm 12.2.2).
- Backend: Spring Boot 4.1.2 / Java 25, H2 in-memory para dev/tests; arranque con perfil `dev,postgres` (PostgreSQL via Podman). Arrancado con Maven 3.10.1 local (`.build/maven`) y tests con JUnit 6. Se ejecutan con JDK 25 (`C:\Users\Ivan_\dev\tools\jdk-25\jdk-25.0.5+8`).
- Usuarios de test (password: `test1234`):
  - ana@armario.test — perfiles: Ana (MUJER), Peque (NINO, compartido con Carlos)
  - carlos@armario.test — perfiles: Carlos (HOMBRE), Bebe (BEBE, compartido con Ana)
  - abuela@armario.test — perfil: Abuela (MUJER)

## Casos de test (v0.2 — auth)

| ID | Caso | Resultado | Fecha | Notas |
|---|---|---|---|---|
| T-AUTH-01 | Registro con email nuevo | PASS | 2026-11-16 | `ApiIntegrationTest`: 201 + token |
| T-AUTH-02 | Registro con email duplicado (409) | PENDIENTE | — | — |
| T-AUTH-03 | Login correcto | PASS | 2026-11-16 | ana@armario.test; token 36 chars |
| T-AUTH-04 | Login incorrecto (401) | PASS | 2026-11-16 | password errónea |
| T-AUTH-05 | Login con Google (ID token valido) | PENDIENTE | — | Requiere GOOGLE_CLIENT_ID real; backend valida aud (`armario.google.client-id`) |
| T-AUTH-06 | Login con Google (token invalido) | PENDIENTE | — | — |
| T-AUTH-07 | Logout invalida el token | PASS | 2026-11-16 | 204 y token deja de valer |
| T-AUTH-08 | `GET /api/auth/me` con token y sin token | PASS | 2026-11-16 | 200 con email; 401 sin token |
| T-AUTH-09 | La sesion se mantiene al recargar | PASS | 2026-11-16 | `restore()` + `/api/auth/me`; recarga muestra Inventario y usuario |
| T-AUTH-10 | Limite de usuarios por instalacion (3) | PASS | 2026-11-16 | `AuthLimitTest`: 3 registros OK, 4to en 403 "limite de usuarios alcanzado"; `armario.max-users` configurable |
| T-PROF-01 | Listar perfiles del usuario | PASS | 2026-11-16 | 3 perfiles para Ana |
| T-PROF-02 | Perfil compartido visible para ambos usuarios | PASS | 2026-11-16 | Ambos usuarios ven 3 perfiles |
| T-PROF-03 | Crear perfil | PASS | 2026-11-16 | `ApiIntegrationTest`: 201 y aparece en el listado |
| T-PROF-04 | Compartir perfil con otro usuario | PASS | 2026-11-16 | UI "Compartir" + Carlos ve el perfil |
| T-PROF-05 | Crear perfil desde la UI | PASS | 2026-11-16 | "Peque QA" NINO creado y seleccionado |
| T-SEC-01 | Sesion autenticada accede a /api/profiles | PASS | 2026-11-16 | 401 sin token/sesion |
| T-PREN-01 | Listar prendas de un perfil | PASS | 2026-11-16 | Vía combinaciones y carga de perfil |
| T-PREN-02 | Crear prenda en perfil propio | PASS | 2026-11-16 | API (integration) y flujo UI de captura |
| T-PREN-03 | Crear prenda en perfil compartido | PENDIENTE | — | Acceso por sharedWith |
| T-PREN-04 | Denegar acceso a prenda de perfil ajeno (403) | PASS | 2026-11-16 | PUT y DELETE devuelven 403 |
| T-PREN-05 | Eliminar prenda | PASS | 2026-11-16 | DELETE 204 en API y UI (7 prendas tras borrar) |
| T-PREN-06 | Actualizar estado de prenda | PASS | 2026-11-16 | PUT estado LAVANDERIA; chip y filtro actualizados |
| T-VIS-01 | Analizar foto y devolver color dominante | PASS | 2026-11-16 | PNG plano #3b5bdb devuelve `#3b5bdb` exacto (`VisionControllerTest` + HTTP) |
| T-VIS-02 | Crear prenda desde foto con sugerencias | PASS | 2026-11-16 | Modal → sugerencia `#3b5bdb · SUPERIOR · algodon` → Guardar → 9 prendas |
| T-VIS-03 | Imagen invalida devuelve 400 | PENDIENTE | — | — |
| T-VIS-04 | La captura guarda la descripcion (color/tipo/tejido) sin imagen | PASS | 2026-11-16 | `capturaGuardaDescripcionDePrendaSinGuardarImagen`: color/tejido en prenda, `imagenUrl` null |
| T-COM-01 | Generar combinaciones con superior + inferior | PASS | 2026-11-16 | Unit: CombinacionServiceTest |
| T-COM-02 | Combinaciones respetan armonia de color | PASS | 2026-11-16 | Unit: neutro y complementario |
| T-COM-03 | Perfil sin prendas devuelve lista vacia | PASS | 2026-11-16 | Unit: sin inferior no genera |
| T-COM-04 | Denegar combinaciones de perfil ajeno (403) | PASS | 2026-11-16 | Carlos sobre perfil de Ana |
| T-COM-05 | Pestana Combinaciones en UI muestra outfits | PASS | 2026-11-16 | 3 outfits con 5 prendas en navegador (800×527) |
| T-COM-06 | Filtros de combinaciones por categoria y color | PASS | 2026-11-16 | Color `#3b5bdb` → 3; categoría JOYAS → 0; contador correcto |
| T-ORD-01 | Sugerir revisar prendas sin uso >180 dias | PASS | 2026-11-16 | `OrdenServiceTest` + integracion: Trenca con 200 dias → REVISAR/Baja |
| T-ORD-02 | No sugerir prendas usadas recientemente | PASS | 2026-11-16 | `OrdenServiceTest` con 90 dias → lista vacia |
| T-ORD-03 | Prioridad por antiguedad (ALTA/MEDIA/BAJA) | PASS | 2026-11-16 | 400/300/200 dias ordenados |
| T-ORD-04 | Reponer categorias con <2 disponibles | PASS | 2026-11-16 | Perfil Bebe sin prendas → 3 reposiciones Alta |
| T-ORD-05 | Prendas no disponibles no cuentan como stock | PASS | 2026-11-16 | Estado LAVANDERIA descuenta disponible |
| T-ORD-06 | Usar fecha de captura si nunca se uso | PASS | 2026-11-16 | 365 dias → prioridad Alta |
| T-ORD-07 | Denegar ordenes de perfil ajeno (403) | PASS | 2026-11-16 | Carlos sobre perfil de Ana |
| T-ORD-08 | Pestana Ordenes en UI | PASS | 2026-11-16 | 1 sugerencia (Trenca); contador y filtros propios |
| T-ORD-09 | "Marcar en uso" actualiza ultimoUso y recarga | PASS | 2026-11-16 | Trenca → En uso y 0 sugerencias |
| T-ORD-10 | "Capturar prenda" precarga categoria y recalcula | PASS | 2026-11-16 | Bebe → SUPERIOR; "Body QA" creada y reposicion pasa a Media |
| T-ORD-11 | Combinaciones ignoran prendas no disponibles | PASS | 2026-11-16 | Camisa en lavanderia excluida del outfit |
| T-ASSET-01 | Hero de login se oculta si falta hero.png | PASS | 2026-11-16 | `(error)` + signal heroHidden |
| T-ASSET-02 | Fondo usa textura-pastel.png con respaldo pastel | PASS | 2026-11-16 | `background-color` + `background-image` |
| T-ASSET-03 | Tarjeta de prenda usa placeholder vectorial | PASS | 2026-11-16 | `prenda-placeholder.svg` |
| T-ASSET-04 | Assets copiados a `dist/.../browser/assets/armario` | PASS | 2026-11-16 | Verificado tras `ng build` |
| T-PLAT-01 | Tauri inicializa y detecta entorno (Rust/WebView2) | PASS | 2026-11-16 | `npx tauri info` |
| T-PLAT-02 | `cargo check` de src-tauri | PASS | 2026-11-16 | 427 crates; compila en 1m21s |
| T-PLAT-03 | Capacitor sincroniza assets (Android/iOS) | PASS | 2026-11-16 | `cap add/sync android`; `cap add ios` |
| T-PLAT-04 | Token Bearer aceptado por el backend | PASS | 2026-11-16 | login, perfiles y combinaciones |
| T-PLAT-05 | Cache offline devuelve datos sin red | PASS | 2026-11-16 | `fetch` bloqueado → cache muestra 3 combinaciones |
| T-PLAT-06 | Aviso "Sin conexion" visible | PASS | 2026-11-16 | Evento `offline` → "Sin conexión · datos locales" |
| T-UI-01 | Sin desbordamiento horizontal | PASS | 2026-11-16 | `box-sizing: border-box` global; `scrollWidth` 800 = viewport 800 |
| T-UI-02 | Plural correcto en contadores | PASS | 2026-11-16 | "1 combinación" / "3 combinaciones" |

## Mockups

- `Armario y Combinaciones_ seis mundos visuales.html` — 6 mundos visuales.
- Mundo elegido: Pop Culture Shelf (#2).

## Historial

- 2026-11-16: auth backend (register/login/google) + pantalla login frontend + seed de 3 usuarios test. Sin ejecucion de tests aun.
- 2026-11-16: perfiles backend (GET/POST/share) + SessionAuthFilter + sesion en login/register/google. Backend compila. Tests aun PENDIENTES (sin arranque real).
- 2026-11-16: prendas por perfil (entidad Prenda, PrendaRepository, endpoints GET/POST/DELETE) + UI con selector de perfil. Backend compila. Tests PENDIENTES.
- 2026-11-16: captura por foto (mock): VisionController con color dominante, UI de captura/confirmacion. Backend compila. Tests PENDIENTES.
- 2026-11-16: combinaciones: CombinacionService (armonia de color HSB), GET /api/combinaciones, pestana Combinaciones en UI, seed de prendas de ejemplo. Backend compila. Tests PENDIENTES.
- 2026-11-16: verificacion: `ng build` OK (bundle 295 kB); 6/6 tests unitarios de CombinacionService en verde (JUnit 6 via launcher); backend `javac` OK. T-COM-04/05 siguen pendientes de runtime.
- 2026-11-16: UI combinaciones con filtros por categoria y color (client-side). `ng build` OK (296 kB). T-COM-06 pendiente de prueba manual.
- 2026-11-16: responsive Pop Culture (media query 640px: grid 2 columnas, combos 1 columna, barra y modal ajustados). `ng build` OK (297 kB).
- 2026-11-16: assets integrados: carpeta `public/assets/armario` (contrato + prompts), hero de login con ocultacion por error, textura pastel con respaldo, placeholder SVG de prenda. `ng build` OK (298 kB) y assets copiados a `dist`. Imagenes reales pendientes de que el usuario las genere.
- 2026-11-16: multiplataforma: Tauri v2 inicializado (identifier propio, ventana 1100x760) y `cargo check` OK; Capacitor con plataformas Android e iOS y `cap sync` OK. Auth con token Bearer + logout, `API_BASE` por entorno, cache offline por perfil y aviso de desconexion. `ng build` OK (301 kB). Pendiente probar token/offline con backend en ejecucion.
- 2026-11-16: backend arrancado con Maven 3.10.1 (descargado en `.build`): `mvn test` 6/6 OK y pruebas HTTP: login 200, login malo 401, sin token 401, perfil ajeno 403, combinaciones 3 outfits (5 prendas), logout 204 e invalida token. Corregido `deleteByToken` con transaccion.
- 2026-11-16: QA: `ApiIntegrationTest` (7 casos: registro/login/perfil/prendas/combinaciones, 401, logout, `/api/auth/me`, 403 y perfil compartido), `VisionControllerTest` (3) y tests frontend de `CacheService`, `CombinacionService` y `AuthService` (8). Total 16 backend + 8 frontend en verde.
- 2026-11-16: QA en navegador (800×527): login, recarga con sesion restaurada, 3 outfits, filtros color/categoria, offline + cache, captura con vision (`#3b5bdb`) y alta de prenda (9 prendas). Corregidos: restauracion de sesion (`GET /api/auth/me`), `401` JSON en seguridad, desbordamiento horizontal (`box-sizing`), plural de contadores y color dominante cuantizado (ahora media real del bucket).
- 2026-11-16: iteracion 8.1: `PUT /api/prendas/{id}`, UI de inventario con cambio de estado, borrado con confirmacion y filtros reales. Tests: 18 backend (9 integracion) y 13 frontend en verde; verificado en navegador (estado Lavanderia, filtro 1 prenda, borrado 8→7).
- 2026-11-16: iteracion 8.2: modal de perfiles (crear y compartir), cache de perfiles y tests de `ProfileService` (13 frontend). Verificado en UI: "Peque QA" creado y compartido con carlos@armario.test.
- 2026-11-16: iteracion 8.3: `OrdenServiceTest` (6 casos), 2 tests nuevos de integracion (`/api/ordenes/generar` con acceso y 403; combinaciones sin prendas no disponibles) y 2 tests de `OrdenService` en frontend. Total: 26 backend (11 integracion) y 15 frontend en verde; verificado en navegador (Trenca 200 dias → Marcar en uso → 0 sugerencias; perfil Bebe → 3 reposiciones → Capturar prenda crea "Body QA" y recalcula).
- 2026-11-16: iteracion 8.4: PostgreSQL levantado con Podman (`armario-postgres`) y backend arrancado con perfiles `dev,postgres`; persistencia verificada (prenda creada, reinicio y recarga OK); suite backend en verde (26 tests, H2).
- 2026-11-16: iteracion 8.5: limite de usuarios (3) implementado en `AuthController` (register + alta Google con 403); `ApiIntegrationTest` con `armario.max-users=50`; nuevo `AuthLimitTest` (limite 3). Suite backend en verde (27 tests: 12 integracion).
- 2026-11-16: iteracion 8.6: solo se guarda la descripcion de la captura (color/tipo/tejido); no se persiste la imagen. `Prenda.tejido` + `PrendaRequest.tejido` + envio desde la vision en captura; test `capturaGuardaDescripcionDePrendaSinGuardarImagen`. Suite backend en verde (28 tests: 12 integracion).
- 2026-11-16: iteracion 8.8: se quita el login con Google (frontend: boton, script GSI, `loginWithGoogle`; backend: `POST /api/auth/google`) para la release Android-only (solo email/password). Suite backend (28 tests) y frontend (15 tests) en verde; `ng build` OK.
- 2026-11-16: iteracion 8.7 (continua): Google Sign-In con client id configurable — backend valida `aud` del ID token contra `armario.google.client-id`; frontend lee `armario.googleClientId` (localStorage) con fallback. Suite backend/frontend en verde. Pendiente credencial real (`GOOGLE_CLIENT_ID`) y flujo GSI real (T-AUTH-05/06 PENDIENTES).