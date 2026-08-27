# E1 — Ciclo de vida de una petición MVC

Equipo: `01` — Integrantes: `___`, `___`, `___`
Fecha: `2026-__-__`

## Instrucciones

Ponga un punto de interrupción en `LoteWebController.listar` y otro en
`LoteService.buscar`. Abra `http://localhost:8080/lotes` en modo depuración y
complete la columna «Qué observó en el depurador» con lo que vea en su máquina
(pila de llamadas, valor de `estado`, número de lotes que devuelve el servicio).
Las tres primeras columnas ya están rellenas con las clases reales de este
proyecto y del framework.

## Tabla de trazado — `GET /lotes`

| # | Paso | Clase que lo ejecuta | Archivo | Qué observó en el depurador |
|---|---|---|---|---|
| 1 | Apertura TCP y envío de la petición (línea de petición + cabeceras, RFC 9110) | (navegador Chrome/Edge) | — | _(p. ej. `GET /lotes HTTP/1.1`, `Accept: text/html`)_ |
| 2 | Aceptación de la conexión y creación de `HttpServletRequest` / `HttpServletResponse` | Tomcat 11 embebido: `org.apache.coyote.http11.Http11Processor` → `org.apache.catalina.connector.CoyoteAdapter` | dependencia `spring-boot-starter-tomcat` (no es código propio) | _(hilo `http-nio-8080-exec-N`)_ |
| 3 | Cadena de filtros | `ec.edu.uteq.agrotrace.common.security.JwtAuthFilter` (extiende `OncePerRequestFilter`) + cadena de Spring Security configurada en `SecurityConfig` | `src/main/java/ec/edu/uteq/agrotrace/common/security/JwtAuthFilter.java` y `common/security/SecurityConfig.java` | _(en `/lotes` no hay cabecera `Authorization`, así que el filtro solo hace `cadena.doFilter(...)`)_ |
| 4 | Front controller: único punto de entrada | `org.springframework.web.servlet.DispatcherServlet` (registrado por `DispatcherServletAutoConfiguration`) | dependencia `spring-boot-webmvc` | _(método `doDispatch`)_ |
| 5 | Resolución del manejador (verbo + ruta → método Java) | `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` | dependencia `spring-webmvc` | _(resuelve a `LoteWebController#listar`)_ |
| 6 | Invocación del método del controlador y conversión de parámetros | `RequestMappingHandlerAdapter` → `LoteWebController.listar(EstadoLote, Model)` | `src/main/java/ec/edu/uteq/agrotrace/lote/web/LoteWebController.java` | _(`estado == null` si no se filtró; `model` vacío al entrar)_ |
| 7 | Delegación a servicio y repositorio; Hibernate traduce a SQL contra PostgreSQL | `LoteService.buscar(EstadoLote)` → `LoteRepository` (proxy `org.springframework.data.jpa.repository.support.SimpleJpaRepository`) → Hibernate 7 → driver `org.postgresql` | `lote/app/LoteService.java`, `lote/domain/LoteRepository.java` | _(el `SELECT ... FROM lote ORDER BY fecha_recepcion DESC` en el log SQL; devuelve N lotes)_ |
| 8 | Renderizado de la vista | El controlador devuelve el nombre lógico `"lotes/lista"`; `ThymeleafViewResolver` lo resuelve a `templates/lotes/lista.html` y `SpringTemplateEngine` la renderiza a HTML | `src/main/resources/templates/lotes/lista.html` | _(el `Model` lleva `lotes`, `estados`, `estadoSeleccionado`)_ |
| 9 | Escritura de la respuesta y liberación de la conexión | `DispatcherServlet` → `HttpServletResponse` → Tomcat (`CoyoteAdapter`) | dependencia `spring-boot-starter-tomcat` | _(HTTP 200, `Content-Type: text/html;charset=UTF-8`)_ |

> Nota: en esta página, una vez cargado el HTML, `static/js/tablero.js` hace además
> un `fetch` a `GET /api/v1/lotes` (Bloque 4). Ese es un **segundo** ciclo de petición
> completo que atraviesa el `DispatcherServlet` hasta `LoteRestController`, esta vez
> con un `HttpMessageConverter` (Jackson → JSON) en el paso 8 en lugar del
> `ViewResolver`.

## Pregunta de cierre

**¿En qué se diferencia este ciclo del MVC original de Reenskaug (1979)?**

En el MVC original (y en la formulación de Krasner y Pope de 1988) la vista **observa**
al modelo: se suscribe a sus cambios y se redibuja sola cuando el modelo emite un
evento. Ese mecanismo de notificación es el corazón del patrón. En la web clásica esa
propiedad se pierde porque HTTP es **sin estado**: no hay una vista viva a la que
notificar. La vista se genera entera en cada petición y el ciclo termina cuando se
envía la respuesta; el servidor «olvida» todo. Lo que llamamos MVC en la web es en
realidad el patrón *Model 2* / *Front Controller* que describió Fowler [4]: un único
controlador frontal (`DispatcherServlet`) recibe todas las peticiones y las despacha.
Conservamos el nombre por tradición, pero la observación solo reaparece —muy
transformada— en los frameworks de interfaz reactiva del lado del cliente.
