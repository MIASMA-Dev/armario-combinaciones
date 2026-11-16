# Roadmap de versiones — Armario y Combinaciones

> Estado actual: v0.6 completada (8.1–8.7 cerradas: prendas/perfiles UI, órdenes, PostgreSQL, limite 3 usuarios, captura con descripción, Google Sign-In con client id configurable). Pendiente: credencial real `GOOGLE_CLIENT_ID`, imágenes definitivas, shells Tauri/Capacitor en runtime y build iOS (macOS).

## v0.1 — Base
- Backend Spring Boot + frontend Angular skeleton.
- Consumo de libreria del proyecto 11 (InMemory).
- Diseño Pop Culture Shelf (HTML de referencia).
- Prompts de imágenes y esquemáticos.

## v0.2 — Persistencia y usuarios
- BD (PostgreSQL/H2) sustituyendo InMemory.
- Modelo User + auth basica (JWT).
- Modelo Profile (HOMBRE/MUJER/NINO/BEBE) con compartición entre usuarios.
- Seed de 3 usuarios y perfiles compartidos de niño/bebé.
- Item/Orden/Combinacion asociados a profileId.

## v0.3 — Combinaciones
- Atributos de prenda: tipo (superior/inferior/calzado/abrigo), color, talla.
- GenerarCombinacionesUseCase por color y prenda.
- Endpoint y UI de combinaciones por perfil.
- Filtros por color/temporada/estado.

## v0.4 — Multiplataforma
- Tauri v2: app de escritorio configurada (`npm run tauri:dev` / `tauri:build`).
- Capacitor: Android e iOS añadidos (compilar iOS requiere macOS).
- Sync basica: token Bearer, cache local por perfil y aviso offline (verificado).

## v0.5 — Diseño y assets
- Aplicar Pop Culture Shelf al frontend completo.
- Layout responsive desktop/movil.
- Integrar assets (carpeta `frontend/public/assets/armario` con respaldos vectoriales).

## v1.0 — Release
- Tests backend/frontend (16 backend + 8 frontend en verde).
- Verificacion en navegador (desktop/movil responsive); shells nativos pendientes de runtime.
- Documentacion final y empaquetado.