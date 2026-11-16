# Especificación Funcional — Armario y Combinaciones

## 1. Usuarios y perfiles
- Hasta 3 usuarios por instalación (hogar compartido).
- Cada usuario puede tener varios perfiles: HOMBRE, MUJER, NINO, BEBE.
- Los perfiles NINO/BEBE pueden compartirse entre usuarios (ej. ambos padres).
- Las prendas, combinaciones y órdenes se asocian a un perfil, no al usuario.

## 2. Captura de prendas
- Registrar prenda: nombre, color, temporada, talla, tipo (superior, inferior, calzado, abrigo, chaquetas, ropa interior, calcetines, complementos, joyas, sombreros), imagen opcional.
- Estados: disponible, en uso, lavandería, pendiente.
- Listado con filtros por color, temporada, estado, perfil, categoria de prenda.

## 3. Combinaciones
- Generar combinaciones (outfits) por color y prenda.
- Distinguir categorias: superior, inferior, ropa interior, calcetines, zapatos, complementos (bufandas, fulares...), joyas, sombreros.
- Generacion automatica por armonia de colores (complementarios, analogos, monocromo, neutro) y compatibilidad de categorias.
- Cada combinacion parte de un superior + un inferior compatible y añade calzado, abrigo/chaqueta, complemento, joya y sombrero si armonizan.
- Mostrar combinaciones sugeridas (superior + inferior + calzado + abrigo).
- Filtrar por perfil (hombre, mujer, nino, bebe).
- Limite de 24 combinaciones por generacion.

## 2.1 Captura por foto (opcional)
- El usuario puede tomar/seleccionar una foto de la prenda.
- El sistema analiza la imagen y sugiere: color(es) dominantes, tipo de prenda, tipo de tejido.
- El usuario confirma o corrige los valores antes de guardar.

## 4. Órdenes (sistema base)
- Sugerencias de revisión por inactividad (>180 días).
- Sugerencias de reposición (stock bajo) opcionales.

## 5. Sincronización
- Sincronización por usuario entre dispositivos (escritorio y móvil).
- Perfiles compartidos se sincronizan entre usuarios autorizados.

## 6. Plataformas
- Web responsive, escritorio (Tauri), móvil (Capacitor).