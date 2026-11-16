# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Stack

Angular 22 (frontend) + Spring Boot 4.1.2 / Java 17 (backend), consumiendo la libreria del proyecto 11 (core-domain, core-application, infra-*). Empaquetado multiplataforma: Tauri (escritorio) + Capacitor (movil). Persistencia con BD (sustituye repositorios in-memory) para sincronizacion por usuario.

## Users

Hasta 3 usuarios por instalacion (hogar compartido). Cada usuario puede tener varios perfiles (hombre, mujer, nino, bebe). Los perfiles de nino/bebe pueden compartirse entre usuarios (ej. ambos padres gestionan la ropa del nino). Las prendas y combinaciones se asocian a un perfil, no directamente al usuario.

## Product Purpose

Organizar el armario y generar combinaciones (outfits) por color y prenda para hombre, mujer, nino y bebe. El exito se mide por combinaciones utiles y sincronizacion correcta entre dispositivos por usuario.

## Positioning

Inventario personal de ropa con sugerencias derivadas del uso y la inactividad (prendas sin uso prolongado), en lugar de una simple lista de prendas.

## Operating Context

Uso en escritorio (Tauri) y movil (Capacitor) con sincronizacion por usuario. Captura manual de prendas con imagen opcional. Generacion de combinaciones por color y prenda. Generacion de ordenes bajo demanda.

## Capabilities and Constraints

- Captura y listado de items de tipo ROPA (prendas) con atributos: color, temporada, talla, genero/edad (hombre, mujer, nino, bebe), categoria de prenda (superior, inferior, ropa interior, calcetines, zapatos, complementos, joyas, sombreros, chaquetas, abrigos).
- Generacion automatica de combinaciones por color y categoria de prenda.
- Captura por foto con deteccion automatica de color(es), tipo de prenda y tejido (opcional, vía servicio de vision).
- Generacion de combinaciones (outfits) por color y prenda.
- Sincronizacion por usuario (hasta 3 usuarios) con persistencia en BD.
- Generacion de sugerencias de revision por inactividad (>180 dias) usando la libreria del proyecto 11.
- Imagenes referenciadas por ruta (gestion fisica delegada a infraestructura).

## Brand Commitments

Nombre: "Armario y Combinaciones por Colores". Sin paleta ni tipografia vinculantes definidas aun.

## Product Principles

- Inventario primero: la fuente de verdad es la lista de prendas capturadas.
- Sugerencias accionables: toda orden debe explicar su motivo.
- Simplicidad: flujos cortos de captura y consulta.
- Reutilizacion de la base comun del proyecto 11 sin duplicar dominio.