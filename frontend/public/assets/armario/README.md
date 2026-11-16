# Assets — Armario y Combinaciones

Carpeta para las imágenes del mundo visual **Pop Culture Shelf**. Genera las imágenes con los prompts de `DESIGN.md` / `prompts-imagenes.md` y guárdalas aquí con estos nombres exactos. La app las detecta automáticamente.

| Archivo | Tamaño | Uso en la app | Prompt (DESIGN.md) |
|---|---|---|---|
| `hero.png` | 1920×1080 | Portada de la pantalla de login | 1. Hero / portada |
| `textura-pastel.png` | 1024×1024 (tileable) | Fondo del login y de la app | 2. Textura pastel |
| `prenda-placeholder.png` | 512×512 | Imagen de prenda sin foto | 3. Mascota/prenda |
| `cinta-estado.png` | 512×512 | Reservado para cintas de estado | 4. Cinta de estado |
| `fondo-seccion.png` | 1920×1080 | Reservado para cabeceras de sección | 5. Fondo de sección |

Notas:

- Si falta un archivo, la app usa su respaldo: degradado pastel en fondos y `prenda-placeholder.svg` en tarjetas. No se rompe nada.
- `prenda-placeholder.svg` ya está incluido como respaldo vectorial; puedes sustituirlo por `prenda-placeholder.png` editando la ruta en `frontend/src/app/app.html`.
- Mantén el estilo: pastel suave, keyline blanco, relleno plano, una cinta saturada por estado.
