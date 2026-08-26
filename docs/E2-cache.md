# E2 — Justificación del TTL de la caché

Equipo: `NN`

## 1. Evidencia de la clave almacenada

Pegue aquí la salida de:

```powershell
docker exec -it agrotrace-redis redis-cli KEYS "pronostico-secado*"
docker exec -it agrotrace-redis redis-cli TTL "pronostico-secado::acopio-principal"
```

```
(salida aquí)
```

## 2. Evidencia del acierto de caché

Pegue las dos líneas del log que demuestran que la **primera** llamada golpeó
el origen y la **segunda** no.

```
(salida aquí)
```

## 3. Justificación del TTL — un párrafo

Responda: **¿por qué 15 minutos y no 30 segundos ni 24 horas?** Su argumento
debe referirse a (a) la frecuencia con que el dato se actualiza en el origen y
(b) el coste de servir un dato obsoleto **en este dominio concreto**, es decir,
qué le pasa al técnico de acopio si decide sacar el cacao a la marquesina con
un pronóstico viejo.

> _Su respuesta aquí._

## 4. Degradación

Describa qué ocurre cuando el origen no responde y por qué esa decisión es
mejor que devolver un error al tablero completo.

> _Su respuesta aquí._
