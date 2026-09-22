# Regla 1 · Deduplicación de alertas por fingerprint

> _"Una alerta no es un incidente."_ — `docs/modelo.md`, bloque 2

Esta es la primera de las cuatro reglas que el generador no podía adivinar. El modelo ya tenía
el campo `fingerprint` y la relación `Alerta → Incidente`; lo que faltaba era **quién decide** y
**con qué criterio**.

---

## El problema

Un disco se llena. Prometheus dispara una alerta. Treinta segundos después dispara otra, y otra,
y otra. Todas traen el mismo `fingerprint`, porque es la misma condición sobre la misma máquina.

Si cada alerta abriera un incidente, a las tres de la mañana alguien recibiría cuarenta llamadas
por un solo disco. Eso no es un sistema de guardias: es una máquina de quemar gente.

La interrupción es **una**. Las señales son **muchas**. La regla es la que traduce de lo segundo
a lo primero.

---

## El criterio

Cuando entra una alerta, se busca un incidente que cumpla **las tres condiciones a la vez**:

| #   | Condición                    | Por qué                                                                                               |
| --- | ---------------------------- | ----------------------------------------------------------------------------------------------------- |
| 1   | Mismo `fingerprint`          | Es la clave de deduplicación que manda la herramienta de monitoreo.                                   |
| 2   | Mismo `servicio`             | El fingerprint solo no alcanza: `disco-al-90%` en `pagos-api` y en `reportes` son dos interrupciones. |
| 3   | Todavía abierto y en ventana | Estado distinto de `RESUELTO` y `CERRADO`, y detectado dentro de la ventana de deduplicación.         |

- **Si lo encuentra** → la alerta se pega a ese incidente. No se abre nada nuevo.
- **Si no lo encuentra** → la alerta abre un incidente.

En los dos casos la alerta queda asociada a un incidente, se marca `procesada` y **deja una
entrada en `EventoDeIncidente`**. Lo que no se escribe en la línea de tiempo no se puede
reconstruir después en el postmortem.

### La ventana

`oncall.deduplicacion.ventana-minutos` (por defecto **120**) es el piso de la búsqueda: solo se
consideran incidentes detectados dentro de esa ventana hacia atrás.

Existe para un caso concreto: un incidente que quedó abierto por olvido durante una semana no
debería seguir absorbiendo alertas nuevas. Pasada la ventana, la misma señal vuelve a abrir un
incidente, aunque el viejo siga sin cerrarse.

```yaml
oncall:
  deduplicacion:
    ventana-minutos: 120
```

### La severidad

El incidente que se abre no pregunta la severidad: la deriva de la criticidad del servicio
afectado, que ya está en el catálogo.

| Criticidad del servicio | Severidad del incidente |
| ----------------------- | ----------------------- |
| `TIER1`                 | `SEV1`                  |
| `TIER2`                 | `SEV2`                  |
| `TIER3`                 | `SEV3`                  |

Es una traducción deliberadamente simple. Lo importante no es la tabla: es que la decida la
aplicación y no la persona que está media dormida cargando el incidente a mano.

---

## Dónde vive

```
src/main/java/ar/edu/um/isa/oncall/
├── repository/
│   └── IncidenteDeduplicacionRepository.java   # las dos consultas que necesita la regla
├── service/
│   ├── DeduplicacionDeAlertasService.java      # la regla
│   ├── ServicioInexistenteException.java
│   └── dto/
│       ├── AlertaEntranteDTO.java              # la señal cruda que entra
│       └── ResultadoDeduplicacionDTO.java      # qué decidió la regla
└── web/rest/
    └── IngestaDeAlertasResource.java           # POST /api/alertas/ingesta
```

Ninguno de esos archivos lo escribe el generador, y eso es a propósito.

**No tocamos `AlertaService` ni `AlertaResource`.** Esos son el ABM que salió de `jhipster jdl`,
y `POST /api/alertas` tiene que seguir creando una fila y nada más. Si le metiéramos la regla
adentro, el próximo `jhipster jdl oncall.jh --force` se la llevaría puesta —y además romperíamos
los tests generados, que esperan un ABM.

Por la misma razón las consultas nuevas van en `IncidenteDeduplicacionRepository` y no en
`IncidenteRepository`: **el código que escribimos nosotros vive en archivos que el generador no
conoce.**

---

## El endpoint

```
POST /api/alertas/ingesta
```

Es lo que apunta el webhook de Prometheus, Datadog o el healthcheck. Requiere autenticación,
como todo `/api/**`.

**Request**

```json
{
  "servicioId": 1,
  "fingerprint": "disco-lleno-db-01",
  "origen": "PROMETHEUS",
  "resumen": "Disco al 95% en db-01",
  "payload": "{\"instance\":\"db-01\",\"value\":0.95}",
  "recibidaEn": "2026-03-10T03:00:00Z"
}
```

`recibidaEn` es opcional: si no viene, se toma el instante en que entró.

**Response — primera alerta** (`201 Created`)

```json
{
  "alertaId": 1,
  "incidenteId": 1,
  "incidenteTitulo": "Disco al 95% en db-01",
  "accion": "INCIDENTE_ABIERTO",
  "duplicada": false,
  "ocurrencias": 1,
  "motivo": "Incidente abierto: no habia ninguno abierto para el fingerprint 'disco-lleno-db-01' sobre el servicio 'pagos-api'. Severidad SEV1 derivada de la criticidad TIER1 del servicio."
}
```

**Response — segunda alerta, mismo fingerprint** (`201 Created`)

```json
{
  "alertaId": 2,
  "incidenteId": 1,
  "incidenteTitulo": "Disco al 95% en db-01",
  "accion": "ALERTA_DEDUPLICADA",
  "duplicada": true,
  "ocurrencias": 2,
  "motivo": "Alerta duplicada: el fingerprint 'disco-lleno-db-01' ya tenia un incidente abierto sobre el servicio 'pagos-api'. Es la ocurrencia numero 2. No se abrio un incidente nuevo."
}
```

El `incidenteId` es el mismo. Esa es toda la regla.

Si el `servicioId` no está en el catálogo, responde `400` con `error.servicionotfound`: sin
catálogo no hay a quién rutear, y un incidente huérfano es peor que ninguno.

---

## Probarlo a mano

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}' | jq -r .id_token)

ALERTA='{"servicioId":1,"fingerprint":"disco-lleno-db-01","origen":"PROMETHEUS","resumen":"Disco al 95% en db-01"}'

# tres veces la misma señal
for i in 1 2 3; do
  curl -s -X POST http://localhost:8080/api/alertas/ingesta \
    -H "Authorization: Bearer $TOKEN" \
    -H 'Content-Type: application/json' \
    -d "$ALERTA" | jq '{accion, incidenteId, ocurrencias}'
done
```

Sale un `INCIDENTE_ABIERTO` y dos `ALERTA_DEDUPLICADA`, los tres con el mismo `incidenteId`.

---

## Los tests

| Archivo                                                            | Qué prueba                                                       |
| ------------------------------------------------------------------ | ---------------------------------------------------------------- |
| `src/test/java/.../service/DeduplicacionDeAlertasServiceTest.java` | La decisión, con mocks. Sin base de datos, sin Docker. 18 casos. |
| `src/test/java/.../web/rest/IngestaDeAlertasResourceIT.java`       | De punta a punta contra PostgreSQL real, vía Testcontainers.     |

```bash
./mvnw test -Dtest=DeduplicacionDeAlertasServiceTest   # no necesita Docker
./mvnw verify                                          # incluye los IT, necesita Docker
```

Vale la pena mirar los dos juntos: el unitario dice **qué decide** la regla, el de integración
dice **qué queda en la base** cuando decide.

---

## Lo que falta

**Concurrencia.** Dos alertas con el mismo fingerprint procesadas en paralelo pueden abrir dos
incidentes: la búsqueda y el alta no son atómicas entre transacciones. Resolverlo bien pide un
índice único o un lock por fingerprint, y queda fuera del alcance de esta clase. Está anotado en
el Javadoc de la clase para que no se pierda.

**Las otras tres reglas.** Resolver quién está de guardia, ejecutar el escalamiento y calcular
MTTA/MTTR siguen pendientes. Esta regla les deja el terreno listo: cuando se abre un incidente ya
hay un servicio asociado, una severidad calculada y una línea de tiempo empezada.
