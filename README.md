# AgroTrace Quevedo — esqueleto de la práctica GA-U4

Trazabilidad de lotes de cacao fino de aroma para APROCAFA (Asociación de
Productores de Cacao Fino de Aroma de Quevedo).

**Asignatura:** Aplicaciones Web — 5.º nivel — Paralelo A
**Unidad IV:** Modelo-Vista-Controlador y Servicios Web
**Docente:** Dr. Gleiston Cicerón Guerrero Ulloa, Ph.D.
**Duración de la práctica:** 3 horas de laboratorio, en equipos de 3

---

## Qué es este repositorio

Un proyecto que **compila y arranca tal como está**, con la base de datos
poblada y **14 tareas `TODO-GA` sin resolver**. Su trabajo en clase consiste en
completar esas 14 tareas siguiendo la guía `GA_U4_AgroTrace.pdf`.

Los métodos sin resolver lanzan `UnsupportedOperationException` a propósito: el
proyecto compila, pero la funcionalidad todavía no existe.

---

## 1. Antes de venir a clase

Verifique que tiene instalado:

```powershell
java -version              # debe decir 21.x o 25.x
docker compose version     # Compose v2
git --version
```

> **Java 17 no sirve.** El código usa *records* y otras construcciones de
> Java 21. Spring Boot 4.1.1 admite de Java 17 a Java 26, pero este esqueleto
> exige 21 o superior.

Prepare el entorno **en casa**, no en el laboratorio: la primera descarga de
dependencias y de imágenes tarda entre 15 y 40 minutos según la red.

```powershell
git clone <URL-DE-ESTE-REPOSITORIO>
cd ga-u4-agrotrace-esqueleto

docker compose up -d
docker compose ps

.\mvnw.cmd -q clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

### Checkpoint 0 — su entorno está listo si

| Comprobación | Resultado esperado |
|---|---|
| `http://localhost:8080/actuator/health` | `{"status":"UP"}` |
| `http://localhost:8080/lotes` | La página carga (aún sin tabla: es TODO-GA-03 y 04) |
| `http://localhost:8080/api/docs` | Swagger UI abre |
| Buscar `TODO-GA` en el IDE | Aparecen las 14 tareas |

Detenga con `Ctrl+C` y traiga el equipo así. **No borre `~/.m2` ni las imágenes
de Docker**: son justamente lo que evita la espera del primer día.

---

## 2. Cómo se ejecuta

```powershell
# Infraestructura (terminal 1)
docker compose up -d

# Aplicación (terminal 2)
.\mvnw.cmd spring-boot:run

# Generar las clases Java del contrato SOAP desde el XSD
# (necesario después de resolver TODO-GA-09)
.\mvnw.cmd generate-sources

# Pruebas
.\mvnw.cmd test

# Compilación completa
.\mvnw.cmd clean verify
```

### Puntos de entrada

| URL | Qué es |
|---|---|
| `http://localhost:8080/lotes` | Vista MVC del listado |
| `http://localhost:8080/lotes/nuevo` | Formulario de registro |
| `http://localhost:8080/api/v1/lotes` | API REST de lotes |
| `http://localhost:8080/api/v1/clima/secado` | Pronóstico cacheado |
| `http://localhost:8080/api/docs` | Swagger UI (OpenAPI 3.2) |
| `http://localhost:8080/ws/certificacion.wsdl` | Contrato SOAP |
| `http://localhost:8080/actuator/health` | Estado de la aplicación |

---

## 3. Las 14 tareas

Busque `TODO-GA` en su IDE. Cada tarea indica en el propio comentario qué
escribir y por qué.

| # | Bloque | Archivo | Qué hay que hacer |
|---|---|---|---|
| 01 | 1 | `lote/domain/Lote.java` | Política de recepción de APROCAFA |
| 02 | 1 | `lote/domain/LoteRepository.java` | Dos consultas derivadas |
| 03 | 1 | `lote/web/LoteWebController.java` | Listar y registrar (partes A y B) |
| 04 | 1 | `templates/lotes/lista.html` | Cuerpo de la tabla con estado coloreado |
| 05 | 2 | `lote/api/LoteRestController.java` | Colección paginada y detalle |
| 06 | 2 | `lote/api/LoteRestController.java` | `201 Created` con `Location` |
| 07 | 2 | `common/api/ManejadorGlobalErrores.java` | `ProblemDetail` 404 y 422 |
| 08 | 2 | `lote/api/LoteRestController.java` | Anotaciones OpenAPI |
| 09 | 3 | `resources/xsd/certificacion.xsd` | Contrato XSD con tipos restringidos |
| 10 | 3 | `soap/CertificacionEndpoint.java` | Endpoint SOAP con `@PayloadRoot` |
| 11 | 3 | `soap/ClienteCertificacion.java` | Cliente `WebServiceTemplate` |
| 12 | 4 | `static/js/tablero.js` | `fetch` con comprobación de `ok` |
| 13 | 4 | `clima/ClimaService.java` | Consumo de Open-Meteo con `RestClient` |
| 14 | 4 | `clima/ClimaService.java` + `application.yml` | Caché y degradación |

> Algunas tareas están divididas en partes (A, B, C) dentro del mismo archivo o
> en archivos relacionados, de modo que la búsqueda de la cadena `TODO-GA`
> devuelve más de 14 líneas. **Las tareas son 14**; las partes pertenecen a la
> misma tarea y se califican juntas.

### Comprobación objetiva del Bloque 1

`src/test/java/.../LotePoliticaTest.java` contiene tres pruebas anotadas con
`@Disabled`. **Al resolver TODO-GA-01, borre esas anotaciones y ejecute
`.\mvnw.cmd test`**: las tres deben pasar. Es su verificación antes de mirar la
pantalla.

---

## 4. Compilación de la carátula LaTeX

> **Criterio de piso P2 de la rúbrica.** El PDF que se sube al SGA debe poder
> regenerarse clonando este repositorio y compilando el `.tex`. Estas son las
> instrucciones exigidas por ese criterio.

- **Motor:** `pdflatex`
- **Procesador de bibliografía:** `bibtex`
- **Archivo principal:** `informe/GA-U4.tex`
- **Bibliografía:** `informe/GA-U4.bib`
- **Pasadas mínimas:** 3 (`pdflatex` → `bibtex` → `pdflatex` → `pdflatex`)

```powershell
cd informe
pdflatex -interaction=nonstopmode GA-U4.tex
bibtex   GA-U4
pdflatex -interaction=nonstopmode GA-U4.tex
pdflatex -interaction=nonstopmode GA-U4.tex
```

**Dependencias TeX Live / MiKTeX:** `babel-spanish`, `lmodern`, `geometry`,
`xcolor`, `graphicx`, `hyperref`, `fontenc`, `inputenc`. Todas están en la
instalación estándar de TeX Live full y de MiKTeX; MiKTeX las descarga sola la
primera vez.

**Antes de entregar, verifique dos cosas abriendo el PDF generado:**

1. Tiene **exactamente una página**.
2. La URL del repositorio aparece **completa en una sola línea**, sin cortes.

Complete únicamente los seis comandos del bloque «DATOS DEL EQUIPO» al inicio
de `GA-U4.tex`. No toque el resto.

---

## 5. Qué se entrega

**Al SGA:** un único PDF de **una sola página** (la carátula), con la URL del
repositorio en una sola línea. Nada más. Un archivo adicional o un PDF de más
de una página invalida la entrega.

**En el repositorio:**

- Las 14 tareas resueltas, sin ningún `TODO-GA` pendiente.
- `docs/E1-ciclo-vida.md` … `docs/E5-anexoC.md` completos.
- `docs/evidencias/` con las salidas de `curl` y los XML de SOAP.
- Al menos **3 commits propios por integrante**, con correo institucional.

---

## 6. Estructura

```
ga-u4-agrotrace-esqueleto/
├── docker-compose.yml          PostgreSQL 16 + Redis 7
├── pom.xml                     Spring Boot 4.1.1, Java 21
├── informe/                    Carátula LaTeX de una página
├── docs/                       Plantillas E1–E5 y evidencias
└── src/main/
    ├── java/ec/edu/uteq/agrotrace/
    │   ├── finca/              Entidad y repositorio de fincas
    │   ├── lote/               domain · app · web · api
    │   ├── soap/               Endpoint, cliente y configuración SOAP
    │   ├── clima/              Consumo externo con caché
    │   └── common/             Errores, seguridad y OpenAPI
    └── resources/
        ├── xsd/                Contrato SOAP (contract-first)
        ├── db/migration/       Flyway: esquema y semilla
        ├── templates/lotes/    Vistas Thymeleaf
        └── static/             JavaScript y CSS del tablero
```

---

## 7. Problemas frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| `Could not resolve dependencies: spring-boot-starter-web-services` | Nombre anterior a Spring Boot 4 | Sin guion intermedio: `spring-boot-starter-webservices` |
| `invalid target release: 21` | `JAVA_HOME` apunta a un JDK 17 | Fije `JAVA_HOME` al JDK 21 o 25 y reabra la terminal |
| `Connection refused: localhost:5432` | Contenedores abajo | `docker compose up -d` |
| No existen `CertificarLoteRequest` ni `CertificarLoteResponse` | Falta generar desde el XSD | `.\mvnw.cmd generate-sources` y recargue el proyecto |
| El WSDL devuelve 404 | El XSD todavía no define los elementos | Resuelva TODO-GA-09 primero |
| `fetch` muestra «cargado» con un error 500 | No se comprobó `respuesta.ok` | Revise TODO-GA-12 |
| La caché nunca acierta | Clave variable o falta `@Cacheable` | Revise TODO-GA-14 |
| `LazyInitializationException` al serializar | Se serializó la entidad | Serialice `LoteResponse`, nunca `Lote` |
| Tildes rotas en la vista | Codificación | Ya está `server.servlet.encoding.force: true` |

### Fecha en la respuesta SOAP

`fechaEmision` es `xs:date`, así que JAXB genera un `XMLGregorianCalendar`:

```java
LocalDate fecha = emitido.getFechaEmision();
respuesta.setFechaEmision(
    DatatypeFactory.newInstance().newXMLGregorianCalendar(fecha.toString()));
```

---

## 8. Servicios externos usados

**Open-Meteo** — `https://api.open-meteo.com/v1/forecast`
Gratuito y **sin clave** para uso no comercial. Coordenadas del centro de
acopio: `-1.0286, -79.4636`.

---

## Licencia

Material docente de la Universidad Técnica Estatal de Quevedo. Uso académico.
