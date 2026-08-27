# E5 — Anexo C: preguntas de análisis

Equipo: `01`

---

## Pregunta 1 — Análisis (sobre el patrón)

El lugar donde estuvimos tentados es el `POST /lotes` del controlador web
(`LoteWebController.registrar`). Cuando el técnico envía el formulario, lo natural
sería «aprovechar» que ya tenemos la humedad y la fermentación a mano y decidir ahí
mismo el estado del lote. No lo hicimos: el controlador solo valida la entrada,
delega y elige vista.

```java
@PostMapping("/lotes")
public String registrar(@Valid @ModelAttribute NuevoLoteForm form,
        BindingResult errores, Model model, RedirectAttributes flash) {

    if (errores.hasErrors()) {                       // único if admitido: decide VISTA
        model.addAttribute("fincas", fincaRepository.findAllByOrderByNombreAsc());
        return "lotes/formulario";
    }
    Lote creado = loteService.registrar(form.toCommand());   // delega
    flash.addFlashAttribute("mensaje",
            "Lote " + creado.getCodigo() + " registrado como " + creado.getEstado());
    return "redirect:/lotes";
}
```

La regla vive en la entidad, en `Lote.evaluarEstado()`, y el constructor de `Lote`
la aplica sola al crearse.

**Qué habría costado ponerla en el controlador web.** En el Bloque 2 construimos la
API REST (`LoteRestController.crear`) sobre el mismo modelo. Esa API también crea
lotes: llama a `loteService.registrar(peticion.toCommand())`, que instancia `Lote`,
que ejecuta `evaluarEstado()`. Si la política (`humedad > 7,5 → SECADO_ADICIONAL`,
etc.) hubiera estado en `LoteWebController`, al llegar al Bloque 2 habríamos tenido
dos opciones, las dos malas: (a) **duplicar** el bloque de `if` en el controlador
REST —dos copias de la misma regla que se desincronizan en cuanto APROCAFA cambie el
umbral—, o (b) pararnos a **refactorizar** para sacar la regla a un sitio común,
que es exactamente el trabajo que nos ahorramos por haberla puesto bien desde el
principio. Además la entidad habría quedado como un contenedor de *getters* y
*setters* sin comportamiento: el antipatrón que Fowler bautizó **modelo de dominio
anémico** [4]. Con la regla en la entidad, la API REST fue una segunda vista sobre
el mismo modelo sin escribir ni una línea de lógica de negocio nueva.

---

## Pregunta 2 — Síntesis (sobre REST)

| Principio de Fielding [9] | ¿Lo cumple? | Evidencia concreta | Coste de cumplirlo |
|---|---|---|---|
| **Sin estado** (cada petición se entiende por sí sola) | Sí | `SecurityConfig` fija `SessionCreationPolicy.STATELESS`; el token viaja en la cabecera `Authorization` en cada llamada; no hay `HttpSession`. `GET /api/v1/lotes?estado=ACEPTADO&size=5` devuelve lo mismo sin importar qué se pidió antes. | — (ya se cumple) |
| **Interfaz uniforme** (recursos sustantivos, métodos y códigos con su semántica, hipermedios) | Parcial | Sustantivos y jerárquicos: `/api/v1/lotes`, `/api/v1/lotes/{codigo}`, sin verbos en la URI. Semántica correcta: `200`, `201` + `Location: .../lotes/LT-000013`, `404` y `422` en `application/problem+json` (RFC 9457). **Falta HATEOAS**: las respuestas no traen enlaces (`_links`) salvo el `Location` del `POST`. | Añadir `spring-hateoas`, envolver cada `LoteResponse` en un `EntityModel` con enlaces *self* y a la finca. En este dominio (dos exportadoras con clientes que ya conocen las rutas) el beneficio es marginal → **no vale la pena**. |
| **Cacheable** (las respuestas se marcan como cacheables o no) | Parcial | Hay caché **de aplicación** en `GET /api/v1/clima/secado` (Redis, TTL 15 min, `@Cacheable`). Pero la API **no emite cabeceras HTTP** de caché; de hecho Spring Security añade `Cache-Control: no-cache, no-store` a todas las respuestas, así que un cliente HTTP no puede reutilizar nada. | Configurar cabeceras por endpoint: un `ETag` en `GET /api/v1/lotes/{codigo}` es barato y defendible (un lote concreto cambia poco tras la recepción); un `max-age` corto en la colección. Coste bajo, beneficio real si las exportadoras consultan a menudo → **sí valdría la pena** para el recurso individual. |

**Conclusión.** De los tres, el único que merece la pena cerrar en este dominio es la
cacheabilidad HTTP del recurso individual (`ETag`), porque las exportadoras
consultan el inventario de forma repetida y el ahorro es directo. HATEOAS es un
coste sin retorno cuando los consumidores son dos y están coordinados.

---

## Pregunta 3 — Evaluación (SOAP frente a REST)

| Criterio | SOAP (nuestra certificación) | REST (nuestra API de lotes) |
|---|---|---|
| Formato del mensaje | XML, sobre SOAP 1.1 con espacios de nombres; verboso (respuesta de un certificado ≈ 560 bytes) | JSON `application/json`; compacto (un lote ≈ 210 bytes) |
| Dónde vive el contrato | WSDL + XSD publicados en `/ws/certificacion.wsdl`; es la fuente de verdad | Especificación OpenAPI 3.1 en `/api/openapi` + Swagger UI en `/api/docs`; descriptiva, no normativa |
| Quién valida y cuándo | El **motor SOAP** (`PayloadValidatingInterceptor`) contra el XSD, **antes** de ejecutar nuestro Java: `CACAO-1` devuelve un SOAP Fault `cvc-pattern-valid` sin tocar el servicio | **Bean Validation** en Java, **después** de deserializar: `{"pesoKg":-5}` devuelve `422` con la lista de campos inválidos |
| Generación de clientes | Automática desde el WSDL (JAXB/`wsimport`, SoapUI): el cliente nace con `CodigoLote = LT-[0-9]{6}` incorporado | Manual, o semiautomática con generadores OpenAPI; las restricciones (`@DecimalMin`, formato) van en la documentación, no en los tipos generados |
| Evolución sin romper clientes | Rígida: cambiar el XSD normalmente obliga a versionar el contrato | Flexible: añadir un campo opcional al JSON no rompe nada; nuestros DTO llevan `@JsonIgnoreProperties(ignoreUnknown = true)` |
| Cacheabilidad | Nula: todo es `POST` sobre `/ws`, sin semántica de caché | Los `GET` son cacheables en principio (hoy no emitimos cabeceras); caché de aplicación en el clima |
| Tamaño del mensaje | Mayor (sobre + cabeceras XML + tipos) | Menor |
| Herramientas necesarias | Cliente SOAP / `WebServiceTemplate` / SoapUI; parser XML | `curl`, navegador, `fetch`; ubicuas |
| Curva de aprendizaje | Alta: WSDL, espacios de nombres, `SOAPAction`, SOAP Fault | Baja |

**Si APROCAFA tuviera que integrar mañana a un tercer comprador que no impone
tecnología, ¿cuál le ofreceríamos y por qué?**

**REST.** Tres razones concretas. (1) **Coste de integración para el comprador**:
JSON sobre HTTP con herramientas que ya tiene cualquier equipo; SOAP le obligaría a
montar un *stack* de cliente XML solo para nosotros. (2) **Comprensibilidad**: la
evidencia experimental de Bogner, Kotstein y Pfaff [12] muestra que el cumplimiento
de las reglas de diseño REST tiene un efecto medible en lo entendible que resulta
una API para desarrolladores que no la escribieron —que es exactamente la situación
de un comprador nuevo. (3) **Evolución barata**: podemos añadir campos a
`LoteResponse` sin coordinar una nueva versión del contrato con cada comprador.
SOAP solo se justifica cuando la contraparte lo exige —como la certificadora
nacional—, porque su ventaja real (contrato rígido validado por el motor y generado
al cliente, según SOAP 1.2 y WSDL 2.0 [7, 8]) solo paga cuando esa rigidez es un
requisito institucional, no una comodidad nuestra.

---

## Pregunta 4 — Evaluación (sobre calidad)

| Característica ISO/IEC 25010:2023 [29] | ¿Mejora o empeora? | Mecanismo concreto de nuestro código | Cómo lo mediríamos |
|---|---|---|---|
| **Eficiencia de desempeño** — comportamiento temporal | Mejora | `@Cacheable(cacheNames = "pronostico-secado", key = "'acopio-principal'")` en `ClimaService.consultar()`: la respuesta de Open-Meteo se sirve desde Redis durante 15 min en vez de repetir la llamada externa | Latencia del endpoint `GET /api/v1/clima/secado` con caché fría vs. caliente (medimos ≈ 1,24 s → ≈ 0,07 s) y ratio de aciertos de caché (`redis-cli INFO stats` → `keyspace_hits / (hits + misses)`) |
| **Fiabilidad** — tolerancia a fallos y capacidad de recuperación | Mejora | `ClimaService.consultarTolerante()` captura `ClimaNoDisponibleException` y `ResourceAccessException` y devuelve `PronosticoSecado.noDisponible()`; el `RestClient` lleva *timeouts* de 3 s (conexión) y 5 s (lectura) para no bloquear hilos | Inyectar fallos del origen (apagar la red o un *proxy* como toxiproxy) y medir el % de respuestas 2xx del endpoint y el tiempo hasta recuperación; cubierto por `ClimaServiceTest` (2 casos: 5xx y fallo de conexión) |
| **Adecuación funcional** — corrección / exactitud del dato | Empeora | Esa misma caché sirve un pronóstico de **hasta 15 minutos de antigüedad**: lo que ve el técnico no es siempre el último dato del origen | Muestrear en paralelo `consultarOrigen()` (en vivo) y `consultar()` (cacheado) para la misma clave y medir la diferencia máxima de temperatura/precipitación; por diseño está acotada por el TTL |

**Justificación.** El diseño acepta a conciencia ese intercambio: en un centro de
acopio el pronóstico de las próximas horas no cambia de forma relevante en 15
minutos, y a cambio se evita castigar a un proveedor gratuito y se protege el
tablero ante sus caídas. Es el mismo razonamiento que la propia guía pone como
ejemplo: la caché mejora la eficiencia de desempeño y empeora la exactitud.
