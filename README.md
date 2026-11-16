# Armario y Combinaciones por Colores

Proyecto para elegir combinaciones de ropa por colores y mantener el orden en el armario.

- Propósito: clasificación por colores/combinaciones, gestión de armario y planificación de outfits.
- Estado: base backend Spring Boot + frontend Angular, con auth, perfiles, prendas, captura por foto (mock) y combinaciones automáticas por color.
- Usuarios de prueba (password `test1234`): `ana@armario.test`, `carlos@armario.test`, `abuela@armario.test`.

## Estructura

- `backend/`: API Spring Boot (Java 25) que usa los JARs de `../11-sistema-base-captura-orden/dist`.
- `frontend/`: app Angular minima (Angular 22).
- `docs/`: especificaciones, plan de micro-subtareas y reporting.

## Funcionalidad actual

- Auth: registro, login y Google Sign-In (requiere `GOOGLE_CLIENT_ID`; ahora eliminado para la release Android, solo email/password).
- Perfiles por usuario y perfiles compartidos (hijo/bebé).
- Inventario de prendas con captura por foto (mock de visión).
- Combinaciones automáticas por categoría y armonía de color (`GET /api/combinaciones?profileId=`).
- Assets del mundo Pop Culture en `frontend/public/assets/armario/` (ver su `README.md`).
- Multiplataforma: escritorio Tauri v2 y Android Capacitor, con caché local y token Bearer.

## Ejecucion

Backend:
```
cd backend
mvn spring-boot:run
```
Si no hay Maven global: `.build/maven/apache-maven-3.10.1/bin/mvn.cmd spring-boot:run` (JAVA_HOME configurado a JDK 25).

Base de datos de desarrollo (PostgreSQL con Podman):
```
./scripts/postgres-up.ps1     # inicia PostgreSQL (armario-postgres, puerto 5432)
# backend: mvn spring-boot:run -Dspring-boot.run.profiles=dev,postgres
```
Por defecto (sin perfil) el backend usa H2 in-memory (para tests). Tests: `mvn test` (H2).

Frontend:
```
cd frontend
npm install
npm start
```

Escritorio (Tauri):
```
cd frontend
npm run tauri:dev     # desarrollo con ng serve
npm run tauri:build   # instalador
```

Android (Capacitor):
```
cd frontend
npm run build
npx cap sync android
npm run cap:android   # abre Android Studio
```

iOS (Capacitor, requiere macOS para compilar):
```
cd frontend
npm run build
npx cap sync ios
npm run cap:ios       # abre Xcode
```
En emulador Android la API se resuelve como `http://10.0.2.2:8080`. En un móvil físico, define la IP del backend:
```
localStorage.setItem('armario.apiBase', 'http://192.168.1.10:8080')
```

Podman (dev):
```
./scripts/postgres-up.ps1 -Build   # opcional si se usa compose con postgres
./scripts/podman-up.ps1 -Build
# Frontend: http://localhost:4200
# Backend:  http://localhost:8080
```
Detener:
```
./scripts/podman-down.ps1
./scripts/postgres-down.ps1
```
