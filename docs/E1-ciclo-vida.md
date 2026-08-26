# E1 — Ciclo de vida de una petición MVC

Equipo: `NN` — Integrantes: `___`, `___`, `___`
Fecha: `____-__-__`

## Instrucciones

Ponga un punto de interrupción en `LoteWebController.listar` y otro en
`LoteService.buscar`. Abra `http://localhost:8080/lotes` en modo depuración y
complete la tabla **con las clases reales de su proyecto**, no con las
genéricas de la guía. La columna «Archivo» debe llevar la ruta desde `src/`.

## Tabla de trazado — `GET /lotes`

| # | Paso | Clase que lo ejecuta | Archivo | Qué observó en el depurador |
|---|---|---|---|---|
| 1 | Apertura TCP y envío de la petición | (navegador) | — | |
| 2 | Aceptación y creación de request/response | | | |
| 3 | Cadena de filtros | | | |
| 4 | Front controller | | | |
| 5 | Resolución del manejador | | | |
| 6 | Invocación del método del controlador | | | |
| 7 | Delegación a servicio y repositorio | | | |
| 8 | Renderizado de la vista o serialización | | | |
| 9 | Escritura de la respuesta | | | |

## Pregunta de cierre

En dos o tres frases: **¿en qué se diferencia este ciclo del MVC original de
Reenskaug (1979)?** Concretamente, ¿qué propiedad del patrón original se pierde
en la web y por qué?

> _Su respuesta aquí._
