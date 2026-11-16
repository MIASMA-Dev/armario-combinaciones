# Especificación Técnica — Armario y Combinaciones

## 1. Stack
- Frontend: Angular 22 (web), empaquetado Tauri (desktop) + Capacitor (mobile).
- Backend: Spring Boot 4.1.2 / Java 17, REST.
- Librería base: proyecto 11 (core-domain, core-application, infra-*).
- Persistencia: PostgreSQL/H2 (sustituye InMemory) para sync por usuario.

## 2. Modelo de datos
- User (id, nombre, email, passwordHash)
- Profile (id, nombre, tipo: HOMBRE/MUJER/NINO/BEBE, ownerId, sharedWith: List<userId>)
- Item (id, profileId, nombre, categoria, tipoObjeto, cantidad, unidad, ubicacion, estado, fechaCaptura, imagen, metadatos: color, temporada, talla, tipoPrenda)
- TipoPrenda (enum): SUPERIOR, INFERIOR, ROPA_INTERIOR, CALCETINES, ZAPATOS, COMPLEMENTOS, JOYAS, SOMBREROS, CHAQUETAS, ABRIGOS
- Combinacion (id, profileId, prendas: List<Prenda>, esquemaColor)
- Orden (id, profileId, itemRef, categoria, tipoAccion, prioridad, estado, motivo, fechaGeneracion)

## 3. API REST
- POST /api/auth/login, /api/auth/register, /api/auth/google
- GET /api/auth/me (usuario de la sesion o token; 401 si no hay)
- GET/POST /api/profiles, GET /api/profiles/{id}, POST /api/profiles/{id}/share
- GET/POST /api/prendas?profileId=..., PUT/DELETE /api/prendas/{id}
- GET /api/combinaciones?profileId=... (armonias: neutro, monocromatico, analogo, complementario)
- POST /api/vision/analyze (multipart imagen)
- GET /api/ordenes/generar?profileId=... (sugerencias: inactividad >180 dias y reposicion con stock <2)

## 4. Seguridad
- Auth por token Bearer (`Authorization: Bearer <token>`) y por sesion con cookie en web.
- Endpoint `POST /api/auth/logout` invalida token y sesion.
- Autorización: usuario accede a sus perfiles y perfiles compartidos.
- CORS permitido para navegador (`localhost:4200`) y shells nativos (`tauri://localhost`, `http(s)://tauri.localhost`, `capacitor://localhost`, `https://localhost`).

## 5. Multiplataforma
- Tauri v2: `frontend/src-tauri` con `frontendDist` a `dist/armario-combinaciones/browser`.
- Capacitor: `frontend/android` (iOS pendiente de macOS), `webDir` al build de Angular.
- `API_BASE` resuelve la API segun entorno: relativo en web, `localhost:8080` en Tauri/iOS, `10.0.2.2:8080` en emulador Android; editable con `localStorage.armario.apiBase`.
- Sync basica: cache en `localStorage` por perfil (prendas, combinaciones, perfiles), token persistido y aviso de offline.
- Sync completa (offline-first con cola de cambios) queda para v1.0.

## 6. Vision por foto (opcional)
- Integracion con servicio de vision (API externa o modelo local) para detectar color, tipo de prenda y tejido.
- Campos detectados: color(es), tipoPrenda, tejido, confianza.
- El backend expone POST /api/vision/analyze (multipart imagen) -> JSON con sugerencias.

## 7. Diseño
- Mundo visual: Pop Culture Shelf (pastel, tarjetas, cinta por estado).
- Responsive: mobile-first, breakpoints para desktop.