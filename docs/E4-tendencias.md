# E4 — Tendencias y perspectiva profesional

Equipo: `01`

Cada integrante escribe **un párrafo de 80 a 120 palabras**: de las cuatro
tendencias del Bloque 5 (GraphQL frente a REST; pruebas automatizadas de APIs;
modelos de lenguaje aplicados a la prueba de APIs; evolución de APIs en
producción), **¿cuál incorporaría al PFC de su equipo en los próximos seis meses
y por qué?** Cada párrafo cita al menos una referencia de la guía y se refiere a
una característica concreta del propio proyecto.

> Los tres párrafos siguientes son **borradores**: cada integrante debe revisarlos,
> ponerlos con sus palabras y firmar con su nombre. No los entreguen tal cual.

---

## Integrante 1 — `Nombre completo`

Incorporaría **pruebas automatizadas de APIs** basadas en la especificación
OpenAPI. Nuestra API de lotes ya publica su contrato en `/api/openapi` con los
códigos 200, 201, 404 y 422 documentados; una herramienta de *fuzzing* guiado por
búsqueda como EvoMaster [23] podría generar casos a partir de ese contrato y
ejercitar automáticamente los límites de `CrearLoteRequest` (peso negativo,
humedad fuera de rango) sin que nosotros escribamos cada caso a mano. Lo elijo
porque hoy solo tenemos pruebas del dominio (`LotePoliticaTest`) y del cliente
SOAP; la superficie REST está sin cubrir, y Golmohammadi et al. [22] muestran que
es una línea ya madura y con tooling usable en un semestre.

## Integrante 2 — `Nombre completo`

Incorporaría **GraphQL frente a REST**, pero de forma acotada. El tablero del
técnico hace hoy dos llamadas (`/api/v1/lotes` y `/api/v1/clima/secado`) y pinta
solo seis columnas de cada lote; con GraphQL el cliente pediría exactamente esos
campos en una sola petición. El experimento controlado de Brito y Valente [21]
midió que GraphQL reduce el esfuerzo de construir consultas, sobre todo en quien
no conoce la API —el caso de una exportadora nueva. No lo pondría en todo el
sistema: introduce complejidad de caché y de control de coste de consulta, así
que lo probaría solo en el *endpoint* de lectura del tablero y mantendría REST
para el registro de lotes.

## Integrante 3 — `Nombre completo`

Incorporaría disciplina de **evolución de APIs en producción**. Ahora mismo
nuestra API ya está versionada en la ruta (`/api/v1/lotes`) y los DTO de respuesta
ignoran campos desconocidos (`@JsonIgnoreProperties`), pero no tenemos ninguna
comprobación que impida un cambio rompiente al editar `LoteResponse`. El estudio
de Lercher et al. [28] documenta que los cambios rompientes y la gestión de
versiones son el principal punto de dolor de los equipos, por encima del diseño
inicial. En seis meses añadiría al *pipeline* una comparación automática del
`openapi.json` entre ramas (por ejemplo con `oasdiff`) que falle la construcción
si se elimina o cambia el tipo de un campo ya publicado a las exportadoras.
