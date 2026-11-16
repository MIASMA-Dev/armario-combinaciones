---
name: reporting-armario
description: Actualiza el reporting de tests y el estado de tareas del proyecto Armario y Combinaciones. Usar al terminar cada paso de implementacion o ejecucion de pruebas.
---

# Skill: Reporting Armario

## Cuando usar
Al completar una micro-tarea, una iteracion o una ejecucion de tests.

## Archivos
- `docs/reporting/estado-proyecto.md`
- `docs/reporting/tests.md`

## Procedimiento
1. Leer ambos archivos.
2. Actualizar la tabla de versiones/iteraciones con el estado real.
3. Registrar casos de test:
   - ID (T-XXX-NN)
   - Caso
   - Resultado: PENDIENTE / PASA / FALLA
   - Fecha
   - Notas
4. Añadir una entrada al historial con fecha y resumen.
5. No inventar resultados: si no se ejecuto, PENDIENTE.

## Usuarios de test
- ana@armario.test / test1234
- carlos@armario.test / test1234
- abuela@armario.test / test1234

## Formato
Tablas Markdown concisas. No incluir codigo de aplicacion.
