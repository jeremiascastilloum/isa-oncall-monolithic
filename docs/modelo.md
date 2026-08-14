# El modelo, explicado

Cuatro bloques, trece entidades. Cada bloque responde a una pregunta distinta.

---

## Bloque 1 · Catálogo — *¿qué operamos?*

**`Equipo`** → **`Servicio`** → **`ObjetivoDeServicio`**

Un equipo es dueño de uno o más servicios. Cada servicio tiene una criticidad (`TIER1` a `TIER3`) y un entorno, y define sus objetivos de nivel de servicio: cuántos minutos tenemos para reconocer un SEV1, cuántos para resolverlo.

Es la parte más aburrida del modelo y la más importante: sin catálogo, una alerta no se puede rutear a nadie.

---

## Bloque 2 · Detección — *¿qué está pasando?*

**`Alerta`** → **`Incidente`** → **`EventoDeIncidente`**

Acá está la distinción clave del dominio: **una alerta no es un incidente**.

La alerta es una señal cruda que llega de Prometheus, Datadog o un healthcheck. Trae un `fingerprint` que la identifica. Si el mismo disco se llena tres veces en diez minutos, llegan tres alertas con el mismo fingerprint — pero es *un solo* incidente.

El incidente es la interrupción que un humano gestiona. Sus cuatro marcas de tiempo (`detectadoEn`, `reconocidoEn`, `mitigadoEn`, `resueltoEn`) son las que después producen todas las métricas.

El `EventoDeIncidente` es la línea de tiempo: cada cambio de estado, cada escalamiento, cada nota queda registrada. Es lo que permite reconstruir qué pasó cuando escribís el postmortem.

Notar que `Incidente` y `Servicio` están en **muchos a muchos**: si se cae la base de datos, se degradan cinco servicios a la vez, pero el incidente es uno.

---

## Bloque 3 · Guardias — *¿quién atiende?*

**`Rotacion`** → **`TurnoDeGuardia`**
**`PoliticaEscalamiento`** → **`PasoEscalamiento`**

La rotación define el patrón (semanal, diaria) y los turnos son las ventanas concretas: desde tal instante hasta tal otro, la responsable es tal persona. El campo `esReemplazo` cubre el caso real de "me cambiaron la guardia el jueves".

La política de escalamiento es la cadena: paso 1, avisar por push a quien esté de guardia; si en 5 minutos nadie reconoce, paso 2, llamar por teléfono; si en 10 más sigue sin reconocer, paso 3, avisar al líder del equipo. El `repetirVeces` cubre el caso de dar la vuelta completa otra vez.

Fijate que `PasoEscalamiento` puede apuntar a una rotación (*"a quien esté de guardia"*) **o** a una persona concreta. Las dos relaciones son opcionales, y esa es una regla de negocio que el generador no puede validar: exactamente una de las dos tiene que estar cargada.

---

## Bloque 4 · Respuesta y aprendizaje — *¿cómo salimos, y qué aprendimos?*

**`Notificacion`** · **`Postmortem`** → **`AccionCorrectiva`**

Cada intento de avisarle a alguien deja una `Notificacion` con su canal, su estado y sus reintentos. Es auditoría: cuando alguien dice *"a mí nadie me avisó"*, acá está la respuesta.

El postmortem es uno a uno con el incidente, y de él salen las acciones correctivas: los compromisos concretos con responsable y fecha. Es el único bloque del modelo que mira hacia adelante.

---

## Cómo se usa `User`

Las personas no son una entidad nueva. Usamos el `User` que JHipster ya trae con su autenticación, sus roles y su ABM, y lo referenciamos con `with builtInEntity` desde cinco lugares: el comandante del incidente, el responsable del turno, el destinatario directo de un paso de escalamiento, el destinatario de una notificación y el responsable de una acción correctiva.

Es el primer ejemplo concreto de la tesis de la materia: **no reinventamos lo que el framework ya resolvió.**

---

## Las opciones transversales

Las tres últimas líneas del JDL no agregan ninguna entidad y sin embargo cambian toda la aplicación generada:

- `dto * with mapstruct` — cada entidad expone un DTO en vez del objeto JPA. Aparece una capa de mapeo que no escribiste.
- `service all with serviceClass` — se genera una capa de servicio entre el controlador REST y el repositorio. Ese es el lugar donde después van a vivir las cuatro reglas de negocio que faltan.
- `filter Incidente, Alerta, Servicio, Notificacion` — JHipster genera clases `Criteria` y `QueryService` con filtros por cada campo, y el endpoint REST acepta consultas del tipo `?severidad.equals=SEV1&estado.in=ABIERTO,RECONOCIDO`.

Vale la pena generar una vez con estas líneas y una vez sin ellas, para ver la diferencia en la cuenta de archivos.
