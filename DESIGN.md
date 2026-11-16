# Design System — Armario y Combinaciones

> Mundo visual elegido: **Pop Culture Shelf — Character Goods Catalog** (rejilla de personajes redondeados tipo catálogo de bienes de personaje). Ajustar tras la primera build real.

## Platform

web (Angular 22)

## Mode

Operate (app de gestión de armario; la tarea manda, el mundo solo aporta tipo, paleta, densidad y un gesto firma).

## Visual World

Un catálogo de bienes de personaje: una rejilla de mascotas redondeadas rankeadas por popularidad, cada una con placa de nombre, cumpleaños y una línea de personalidad. Aplicado al armario: cada prenda es un "personaje" con su placa (nombre, color, temporada) y estado.

## Color Strategy

Pastel suave como base + keyline blanco alrededor de cada tarjeta + un color de cinta saturado por estado/acción. El color saturado aparece solo donde hay acción o estado.

- Fondo: pastel suave (lavanda/crema)
- Tarjetas: blanco con keyline
- Cinta por estado: presente (azul), consumido (gris), agotado (rojo), reservado (amarillo)
- Acción primaria: un saturado (coral o teal) solo en botones accionables

## Typography

- Placas de nombre: sans redondeada (bubble face) a tamaño cómodo.
- Cuerpo: sans humanista a tamaños legibles.
- Voz de máquina (etiquetas/códigos): monoespaciada pequeña.
- Jerarquía por tamaño y peso, no por ornamentación.

## Grid & Spacing

- Rejilla de tarjetas (grid) que se reflowa en celdas completas.
- Tarjetas con un idea cada una y padding generoso.
- Navegación por barra inferior persistente (thumb-reachable).
- Espaciado consistente basado en múltiplos de 8px.

## Signature Move

Roster rankeado: las prendas se ordenan por uso/popularidad; la tarjeta activa toma el color de su cinta; los retirados (agotados) se imprimen en gris. Pequeño bounce al tomar foco.

## Motion

- Bounce pequeño al tomar foco (spring suave, sin rebote en entrada).
- Reorganización en celdas completas al filtrar/ordenar.
- Transiciones cortas (150-200 ms), easing suave.

## Components (Operate)

- Tarjetas de prenda (card) con placa, imagen, estado.
- Barra inferior persistente con tabs (Inventario/Outfits/Órdenes).
- Botones redondeados con color de acción solo accionable.
- Filtros por color/temporada/estado.
- Tablas/listas con estados claros (vacío, cargando, error).

## Accessibility

- Contraste AA sobre pastel.
- Navegación por teclado en tarjetas y tabs.
- `prefers-reduced-motion`: desactiva bounce.

## Asset Prompts (para generar imágenes)

Usa estos prompts en tu generador de imágenes. Mantén el mundo: pastel suave, keyline blanco, cinta saturada por estado. Guarda los resultados en `frontend/public/assets/armario/` con los nombres indicados (ver `README.md` de esa carpeta).

| Archivo | Uso | Prompt |
|---|---|---|
| `hero.png` (16:9) | Portada del login | 1. Hero / portada de app |
| `textura-pastel.png` (tileable) | Fondo global | 2. Textura pastel |
| `prenda-placeholder.png` (512×512) | Tarjeta sin foto | 3. Mascota/prenda individual |
| `cinta-estado.png` (512×512) | Cintas de estado | 4. Cinta de estado |
| `fondo-seccion.png` (16:9) | Cabeceras de sección | 5. Fondo de sección |

Sin assets reales la app funciona igual: fondos pastel planos y `prenda-placeholder.svg` como respaldo.

1. Hero / portada de app (16:9, 1920x1080): "A grid of rounded mascot characters ranked by popularity, each with a name plate, birthday and one-line personality note, soft pastel grounds, white keyline around every mascot, flat fills, one saturated ribbon color per character, flat illustration style."
2. Textura pastel (1024x1024, tileable): "Seamless soft pastel ground texture with subtle halftone dots, flat even lighting, tileable."
3. Mascota/prenda individual (cuadrado, 512x512): "A single rounded mascot character with name plate and birthday, pastel, white keyline, flat vector illustration, transparent background."
4. Cinta de estado (cuadrado, 512x512): "A saturated ribbon banner shape, flat, solid color, isolated on transparent background."
5. Fondo de sección (16:9): "Soft pastel grid background with faint ruled lines, flat, no objects."