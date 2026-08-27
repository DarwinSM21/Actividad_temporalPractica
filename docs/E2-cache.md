# E2 — Justificación del TTL de la caché

Equipo: `01`

## 1. Evidencia de la clave almacenada

```powershell
docker exec -it agrotrace-redis redis-cli KEYS "pronostico-secado*"
docker exec -it agrotrace-redis redis-cli TTL "pronostico-secado::acopio-principal"
```

```
1) "pronostico-secado::acopio-principal"
898
```

La clave es constante (`'acopio-principal'`, fijada en `@Cacheable(key = ...)`), así que
todas las peticiones del centro de acopio comparten la misma entrada. El TTL parte de
900 s (15 min) y va decreciendo.

## 2. Evidencia del acierto de caché

Primera llamada (caché fría) frente a segunda llamada inmediata:

```
GET /api/v1/clima/secado -> HTTP 200   ~1.24 s      (golpea Open-Meteo)
GET /api/v1/clima/secado -> HTTP 200   ~0.07 s      (servida desde Redis)
```

Log del servidor — la línea aparece **una sola vez**:

```
INFO  e.edu.uteq.agrotrace.clima.ClimaService : Fallo de cache: consultando origen open-meteo
```

## 3. Justificación del TTL — un párrafo

Open-Meteo recalcula el pronóstico horario en el origen aproximadamente cada hora, de
modo que refrescar la caché más a menudo que eso no aporta datos nuevos: un TTL de
30 segundos multiplicaría por 120 las llamadas al proveedor sin ganar una sola
actualización real, y Open-Meteo es un servicio gratuito cuyo uso responsable es parte
del trato. En el otro extremo, un TTL de 24 horas serviría un pronóstico de ayer: el
técnico de acopio decide **a media mañana** si extiende el cacao en las marquesinas o
lo deja bajo techo, y esa decisión depende de la lluvia y la humedad de las próximas
horas; equivocarse porque el dato tenía un día de antigüedad significa grano
remojado o mohoso, que es justo lo que la trazabilidad busca evitar. Quince minutos es
el punto intermedio defendible: nunca sirve un dato más viejo que el propio ciclo de
actualización del origen, y absorbe las decenas de consultas que hacen los usuarios del
centro con una sola llamada externa cada cuarto de hora.

## 4. Degradación

Cuando Open-Meteo no responde (timeout, 5xx o fallo de conexión), `consultarOrigen()`
lanza `ClimaNoDisponibleException` / `ResourceAccessException` y `consultarTolerante()`
los captura: registra un `WARN` y devuelve `PronosticoSecado.noDisponible()`. El
endpoint sigue respondiendo `200` y el tablero muestra «Sin datos meteorológicos por
ahora» en lugar de romperse. Es mejor que devolver un error al tablero completo porque
el clima es **un dato secundario** del tablero: el técnico todavía necesita ver los
lotes, registrar recepciones y consultar la API aunque el pronóstico no esté
disponible. Un fallo de un tercero no crítico no debe convertirse en una caída de la
herramienta de trabajo. (Verificado en `ClimaServiceTest`.)
