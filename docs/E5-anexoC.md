# E5 — Anexo C: preguntas de análisis

Equipo: `NN`

Cada respuesta debe apoyarse en **datos generados por usted durante esta
práctica** y citar al menos una referencia verificable. Las respuestas
puramente descriptivas no alcanzan el nivel exigido.

---

## Pregunta 1 — Análisis (sobre el patrón)

Localice en su código un lugar donde estuvo *tentado* de poner una regla de
negocio en el controlador y no lo hizo. Copie el fragmento y explique qué habría
pasado si la hubiera puesto allí: concretamente, qué le habría costado después,
cuando construyó la API REST del Bloque 2 sobre el mismo modelo.

```java
// fragmento aquí
```

> _Su respuesta aquí._

---

## Pregunta 2 — Síntesis (sobre REST)

Tome tres de los seis principios de Fielding y contrástelos uno a uno contra la
API que construyó hoy.

| Principio | ¿Lo cumple? | Evidencia concreta (ruta, cabecera o fragmento) | Coste de cumplirlo |
|---|---|---|---|
| | | | |
| | | | |
| | | | |

> _Conclusión: ¿valdría la pena cumplir los que no cumple, en este dominio?_

---

## Pregunta 3 — Evaluación (SOAP frente a REST)

Usted implementó el mismo dominio con los dos estilos. Complete la tabla y
responda con ella.

| Criterio | SOAP (su implementación) | REST (su implementación) |
|---|---|---|
| Formato del mensaje | | |
| Dónde vive el contrato | | |
| Quién valida y cuándo | | |
| Generación de clientes | | |
| Evolución sin romper clientes | | |
| Cacheabilidad | | |
| Tamaño del mensaje | | |
| Herramientas necesarias | | |

> **Si APROCAFA tuviera que integrar mañana a un tercer comprador que no impone
> tecnología, ¿cuál de los dos estilos le ofrecería y por qué?**
>
> _Su respuesta aquí._

---

## Pregunta 4 — Evaluación (sobre calidad)

Elija dos características de calidad de ISO/IEC 25010:2023 que su
implementación de hoy **mejora** y una que **empeora**.

| Característica | ¿Mejora o empeora? | Mecanismo concreto de su código | Cómo lo mediría |
|---|---|---|---|
| | | | |
| | | | |
| | | | |

> _Justificación:_
