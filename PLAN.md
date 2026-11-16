# Plan — Armario y Combinaciones (multiplataforma)

## Objetivo
App de escritorio (Tauri) y movil (Capacitor) sincronizada por usuario (hasta 3), con generacion de combinaciones por color y prenda para hombre, mujer, nino y bebe.

## Fase 1 — Persistencia y usuarios
- [ ] 1.1 Sustituir repositorios InMemory por adaptador JPA (PostgreSQL/H2) en backend.
- [ ] 1.2 Modelo User (id, nombre, email, password hash) y auth basica (JWT o session).
- [ ] 1.3 Modelo Profile (id, nombre, tipo: HOMBRE/MUJER/NINO/BEBE, ownerId, sharedWith: lista de userId).
- [ ] 1.4 Seed de 3 usuarios iniciales y perfiles compartidos de nino/bebe.
- [ ] 1.5 Asociar Item, Orden y Combinacion a profileId (no a userId).

## Fase 2 — Combinaciones
- [ ] 2.1 Extender Item con atributos: profileId, tipo de prenda (superior, inferior, calzado, abrigo), color, talla. El perfil determina el genero/edad (HOMBRE, MUJER, NINO, BEBE).
- [ ] 2.2 Caso de uso GenerarCombinacionesUseCase: combinar prendas por color y tipo, filtrando por genero/edad.
- [ ] 2.3 Endpoint GET /api/combinaciones?genero=...&color=...
- [ ] 2.4 UI: pantalla de combinaciones con selector de genero/edad y color.

## Fase 3 — Multiplataforma
- [ ] 3.1 Configurar Tauri para empaquetar el frontend Angular como app de escritorio.
- [ ] 3.2 Configurar Capacitor para empaquetar como app movil.
- [ ] 3.3 Sincronizacion offline-first (cache local + sync al backend).

## Fase 4 — Diseño (Pop Culture Shelf)
- [ ] 4.1 Aplicar diseño Pop Culture al frontend (ya iniciado).
- [ ] 4.2 Adaptar layout responsive para movil/escritorio.
- [ ] 4.3 Generar assets (prompts en prompts-imagenes.md).

## Fase 5 — QA
- [ ] 5.1 Tests backend (combinaciones, auth).
- [ ] 5.2 Tests frontend.
- [ ] 5.3 Verificacion en desktop y movil.