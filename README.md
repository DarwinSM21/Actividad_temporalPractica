# AgroTrace Quevedo — GA-U4

Trazabilidad de lotes de cacao fino de aroma para APROCAFA (Asociación de
Productores de Cacao Fino de Aroma de Quevedo), con **MVC + API REST + servicio
SOAP propio + integración de servicios externos**.

**Asignatura:** Aplicaciones Web — 5.º nivel — Paralelo A
**Unidad IV:** Modelo-Vista-Controlador y Servicios Web
**Docente:** Dr. Gleiston Cicerón Guerrero Ulloa, Ph.D.
**Pila:** Spring Boot 4.1.1 · Java 21/25 LTS · PostgreSQL 16 · Redis 7

Los catorce puntos de trabajo de la práctica están resueltos y verificados
(evidencias en `docs/evidencias/`).

---

## 1. Requisitos

```powershell
java -version              # 21.x o 25.x  (Java 17 NO sirve: el codigo usa records y pattern matching de Java 21)
docker compose version     # Compose v2
git --version
```

Dependencias de compilación de la carátula (TeX Live full o MiKTeX): `babel-spanish`,
`lmodern`, `geometry`, `xcolor`, `graphicx`, `hyperref`, `fontenc`, `inputenc`.

> Si `JAVA_HOME` apunta a un JDK distinto, fíjelo al 21 o 25 y reabra la terminal.

---

## 2. Cómo se ejecuta

```powershell
# Infraestructura (terminal 1)
docker compose up -d
docker compose ps                     # agrotrace-db y agrotrace-redis en "Up (healthy)"

# Generar las clases Java del contrato SOAP desde el XSD
.\mvnw.cmd generate-sources

# Aplicación (terminal 2)
.\mvnw.cmd spring-boot:run

# Pruebas unitarias
.\mvnw.cmd test

# Compilación completa (pruebas incluidas)
.\mvnw.cmd clean verify
```

### Prueba de integración del canal SOAP

`ClienteCertificacionIT` levanta la aplicación y consume su propio endpoint SOAP.
No corre en el `test` normal; se lanza a propósito con la infraestructura arriba:

```powershell
.\mvnw.cmd test -Dtest=ClienteCertificacionIT -Dagrotrace.it=true
```

### Puntos de entrada

| URL | Qué es |
|---|---|
| `http://localhost:8080/lotes` | Vista MVC del listado (y tablero que consume la API por `fetch`) |
| `http://localhost:8080/lotes/nuevo` | Formulario de registro |
| `http://localhost:8080/api/v1/lotes` | API REST de lotes (paginada, filtrable) |
| `http://localhost:8080/api/v1/clima/secado` | Pronóstico de 48 h cacheado en Redis |
| `http://localhost:8080/api/docs` | Swagger UI (OpenAPI 3.1) |
| `http://localhost:8080/api/openapi` | Documento OpenAPI en JSON |
| `http://localhost:8080/ws/certificacion.wsdl` | Contrato SOAP |
| `http://localhost:8080/actuator/health` | Estado de la aplicación |

---

## 3. Qué implementa cada capa

| Bloque | Alcance | Archivos principales |
|---|---|---|
| 1 — MVC | Regla de recepción en la entidad, consultas derivadas, controlador que solo orquesta, vista con estado coloreado | `lote/domain/Lote.java`, `lote/domain/LoteRepository.java`, `lote/web/LoteWebController.java`, `templates/lotes/lista.html` |
| 2 — API REST | Colección paginada + detalle, `201` con `Location`, errores `ProblemDetail` (RFC 9457), OpenAPI | `lote/api/LoteRestController.java`, `common/api/ManejadorGlobalErrores.java` |
| 3 — SOAP | Contrato XSD contract-first con tipos restringidos, endpoint `@PayloadRoot`, validación de esquema, cliente `WebServiceTemplate` | `resources/xsd/certificacion.xsd`, `soap/CertificacionEndpoint.java`, `soap/ConfiguracionSoap.java`, `soap/ClienteCertificacion.java` |
| 4 — Consumo + caché | `fetch` con comprobación de `respuesta.ok`, `RestClient` con tiempos límite, caché *cache-aside* en Redis y degradación elegante | `static/js/tablero.js`, `clima/ClimaService.java`, `application.yml` |

---

## 4. Compilación de la carátula LaTeX

> **Criterio de piso P2 de la rúbrica.** El PDF que se sube al SGA debe poder
> regenerarse clonando este repositorio y compilando el `.tex`.

- **Motor:** `pdflatex`
- **Procesador de bibliografía:** `bibtex` (la carátula de una página no lleva
  citas, así que este paso no produce salida; `informe/GA-U4.bib` queda disponible
  por si se añaden referencias).
- **Archivo principal:** `informe/GA-U4.tex`
- **Orden de comandos:**

```powershell
cd informe
pdflatex -interaction=nonstopmode GA-U4.tex
bibtex   GA-U4
pdflatex -interaction=nonstopmode GA-U4.tex
pdflatex -interaction=nonstopmode GA-U4.tex
```

Antes de entregar, **abra el PDF generado** y verifique:

1. Tiene **exactamente una página**.
2. La URL del repositorio aparece **completa en una sola línea**, sin cortes.

En `GA-U4.tex` solo se editan las líneas del bloque «DATOS DEL EQUIPO».

---

## 5. Qué se entrega

**Al SGA:** un único PDF de **una sola página** (la carátula), con la URL del
repositorio en una sola línea. Nada más.

**En el repositorio:**

- Los catorce puntos de trabajo resueltos.
- `docs/E1-ciclo-vida.md` … `docs/E5-anexoC.md`.
- `docs/evidencias/` con las salidas de `curl`, los XML de SOAP y el WSDL.

---

## 6. Estructura

```
.
├── docker-compose.yml          PostgreSQL 16 + Redis 7
├── pom.xml                     Spring Boot 4.1.1, Java 21
├── informe/                    Carátula LaTeX de una página
├── docs/                       E1–E5 y evidencias
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

## 7. Notas de compatibilidad con Spring Boot 4

La autoconfiguración de Spring Boot 4 está modularizada; el `pom.xml` declara
explícitamente los módulos que la línea base no arrastra: `spring-boot-flyway`
(migraciones al arrancar), `spring-boot-restclient` (`RestClient.Builder`) y
springdoc-openapi 3.x (la línea 2.x no es compatible con Spring Framework 7). El
starter de SOAP es `spring-boot-starter-webservices` (sin guion intermedio) y en
Spring WS 5.x ya no existe `WsConfigurerAdapter`: la configuración implementa
directamente `WsConfigurer`.

---

## 8. Problemas frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| `invalid target release: 21` | `JAVA_HOME` apunta a un JDK 17 | Fije `JAVA_HOME` al JDK 21 o 25 y reabra la terminal |
| `Connection refused: localhost:5432` | Contenedores abajo | `docker compose up -d` |
| No existen `CertificarLoteRequest` ni `CertificarLoteResponse` | Falta generar desde el XSD | `.\mvnw.cmd generate-sources` y recargue el proyecto en el IDE |
| `LazyInitializationException` al serializar | Se serializó la entidad | Se serializa `LoteResponse`, nunca `Lote` |
| Tildes rotas en la vista | Codificación | `server.servlet.encoding.force: true` en `application.yml` |
| PKIX / `unable to find valid certification path` en Maven | Un antivirus con inspección SSL presenta un certificado que el JDK no reconoce | Importe la raíz de ese antivirus al `cacerts` del JDK, o use un *truststore* propio con `-Djavax.net.ssl.trustStore=...` |

### Fecha en la respuesta SOAP

`fechaEmision` es `xs:date`, así que JAXB genera un `XMLGregorianCalendar`:

```java
DatatypeFactory.newInstance().newXMLGregorianCalendar(fecha.toString());
```

---

## 9. Servicios externos usados

**Open-Meteo** — `https://api.open-meteo.com/v1/forecast` · gratuito y **sin
clave** para uso no comercial. Coordenadas del centro de acopio: `-1.0286, -79.4636`.

---

## Licencia

Material docente de la Universidad Técnica Estatal de Quevedo. Uso académico.
