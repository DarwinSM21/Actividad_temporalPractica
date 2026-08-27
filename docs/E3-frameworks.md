# E3 — Panorama de frameworks MVC y traducción a Laravel

Equipo: `01`

## Tabla 1 — Panorama comparativo (Tema 2, subtema 1)

| Criterio | Spring Boot 4 | Laravel 12 | ASP.NET Core | Django 5 | NestJS 11 |
|---|---|---|---|---|---|
| Lenguaje y versión | Java 21 / 25 LTS | PHP 8.3+ | C# 13 (.NET 9) | Python 3.12+ | TypeScript 5 (Node 20+) |
| ORM | Hibernate / Spring Data JPA | Eloquent | Entity Framework Core | Django ORM | Prisma / TypeORM |
| Motor de plantillas | Thymeleaf | Blade | Razor | Django Template Language | Handlebars / Pug |
| Tipado | Estático fuerte (compilado) | Dinámico (con tipos opcionales) | Estático fuerte (compilado) | Dinámico | Gradual (TS sobre JS dinámico) |
| Gestor de dependencias | Maven / Gradle | Composer | NuGet / `dotnet` CLI | pip / Poetry | npm / pnpm |
| Curva de aprendizaje | Alta (mucha configuración implícita, IoC, capas) | Baja (convención sobre configuración, docs muy guiadas) | Media | Baja–media | Media (patrones de Angular en el backend) |
| Nicho típico de uso | Banca, ERP, sistemas corporativos grandes | PyMEs, MVP, agencias, SaaS pequeño-mediano | Entornos Microsoft / corporativo | Aplicaciones con mucho CRUD y panel de administración; ciencia de datos | APIs y microservicios sobre Node |

## Tabla 2 — Traducción a Laravel (Tema 2, subtema 2)

| Concepto MVC | Lo que escribí hoy (archivo real) | Equivalente en Laravel 12 |
|---|---|---|
| Entidad del modelo | `src/main/java/ec/edu/uteq/agrotrace/lote/domain/Lote.java` (`@Entity`, con `evaluarEstado()`) | `app/Models/Lote.php` — `class Lote extends Model`; la regla iría en un método del modelo o en un *cast*/*accessor* |
| Acceso a datos | `lote/domain/LoteRepository.java` — `interface LoteRepository extends JpaRepository<Lote,Long>` con consultas derivadas | No hay repositorio explícito: se consulta con Eloquent sobre el propio modelo (`Lote::where(...)->orderByDesc('fecha_recepcion')->get()`); si se quiere la abstracción, un *Repository* propio |
| Servicio de aplicación | `lote/app/LoteService.java` (`@Service`, `@Transactional`) | Clase en `app/Services/LoteService.php` resuelta por el *service container*; transacciones con `DB::transaction(...)` |
| Controlador web | `lote/web/LoteWebController.java` (`@Controller`, `@GetMapping("/lotes")`, `@PostMapping`) | `app/Http/Controllers/LoteController.php` + rutas en `routes/web.php` (`Route::get('/lotes', ...)`) |
| Controlador de API | `lote/api/LoteRestController.java` (`@RestController`, `@RequestMapping("/api/v1/lotes")`) | `app/Http/Controllers/Api/LoteController.php` + `routes/api.php` (prefijo `/api` automático); respuestas con *API Resources* |
| Vista | `src/main/resources/templates/lotes/lista.html` (Thymeleaf: `th:each`, `th:text`) | `resources/views/lotes/lista.blade.php` (Blade: `@foreach`, `{{ $lote->codigo }}`) |
| Inyección de dependencias | Constructor + contenedor de Spring (`LoteWebController(LoteService, FincaRepository)`) | *Service container* + *type-hinting* en el constructor del controlador; *auto-wiring* por firma |
| Validación de entrada | `lote/web/NuevoLoteForm.java` y `lote/api/CrearLoteRequest.java` con Bean Validation (`@NotNull`, `@DecimalMin`) + `@Valid` | `app/Http/Requests/CrearLoteRequest.php` — `FormRequest` con el método `rules()` (`'peso_kg' => 'required|numeric|gt:0'`) |
| Filtro / middleware de la petición | `common/security/JwtAuthFilter.java` (un `Filter` en la cadena) | *Middleware* en `app/Http/Middleware/` registrado en `bootstrap/app.php` o en la ruta |
| Migraciones de base de datos | `src/main/resources/db/migration/V1__esquema.sql`, `V2__semilla.sql` (Flyway) | `database/migrations/xxxx_create_lote_table.php` (`php artisan make:migration`), con el *schema builder* de Laravel |
| Documentación de la API | Anotaciones `@Operation` / `@ApiResponses` de springdoc-openapi en `LoteRestController` → `/api/docs` | Paquete `scramble` o `l5-swagger`; o anotaciones en *docblocks* + generación de OpenAPI |
| Autenticación de la API | Spring Security + `JwtAuthFilter` (token *bearer*) | Laravel Sanctum (tokens) o Passport (OAuth2) |
| Construcción / empaquetado | `./mvnw package` → *fat jar* ejecutable | `composer install` + despliegue del código; no hay *fat jar*, el artefacto es el árbol de archivos + `vendor/` |

## Pregunta de cierre

**¿Qué parte de su código de hoy no tendría equivalente directo en Laravel, y por qué?**

El **contrato SOAP contract-first** del Bloque 3 no tiene un equivalente idiomático en
Laravel. En Spring escribimos el XSD (`certificacion.xsd`), Maven generó las clases
`CertificarLoteRequest` / `CertificarLoteResponse` con `jaxb2-maven-plugin`, y
`@Endpoint` + `@PayloadRoot` enrutaron el mensaje por nombre de elemento, con un
`PayloadValidatingInterceptor` que valida contra el esquema antes de que corra
nuestro código. Laravel está construido alrededor de HTTP+JSON y de REST: no trae
generación de artefactos desde WSDL/XSD, ni enrutado por *payload*, ni validación de
esquema XML en el *pipeline*. Para exponer SOAP en Laravel habría que recurrir a una
librería externa de terceros (por ejemplo envolturas sobre `ext-soap` de PHP) y
escribir a mano el mapeo XML↔objeto, perdiendo justamente la propiedad que hace útil
a SOAP aquí: que el contrato rígido y su validación vengan generados y publicados.
El resto del proyecto (MVC, API REST, caché, consumo de terceros) sí traduce casi
línea por línea, como muestra la Tabla 2.
