# OnCall · Sistema de guardias e incidentes

Caso de estudio de **Ingeniería de Software Aplicada** — Universidad de Mendoza.

---

## El problema

Un equipo opera varios servicios en producción. Las herramientas de monitoreo tiran alertas todo el día: la mayoría es ruido, algunas son una interrupción real. Alguien tiene que estar de guardia, alguien tiene que ser despertado a las 3 de la mañana, y cuando el fuego se apaga hay que medir cuánto tardamos y escribir qué aprendimos.

Eso es lo que vamos a construir. No es casual: **es el mismo problema del que trata la materia**. Toda la complejidad que un framework nos permite delegar, después hay que operarla.

---

## Requisitos previos

Traer instalado y funcionando **antes** de la clase:

| Herramienta                                                         | Verificación       |
| ------------------------------------------------------------------- | ------------------ |
| [Docker Desktop](https://www.docker.com/products/docker-desktop)    | `docker --version` |
| [Git](https://git-scm.com/downloads)                                | `git --version`    |
| [Visual Studio Code](https://code.visualstudio.com/download)        | —                  |
| Extensión **Dev Containers** (`ms-vscode-remote.remote-containers`) | —                  |

> ⚠️ La primera vez que abras el Dev Container se descargan varios GB. **Hacelo en casa, no en la facultad.**

---

## Clase 1 · Levantar el entorno

```bash
git clone https://github.com/jeremiascastilloum/isa-oncall-monolithic.git
cd isa-oncall-monolithic
code .
```

Cuando VS Code muestre el aviso _"Folder contains a Dev Container configuration file"_ → **Reopen in Container**.

Si no aparece: `Ctrl+Shift+P` → **Dev Containers: Reopen in Container**.

Al terminar, verificá adentro del contenedor:

```bash
java -version      # 21
node --version     # 22
jhipster --version # 9.2.0
docker ps          # sin errores
```

Con eso alcanza. Todavía no generamos nada.

---

## Clase 2 · Del modelo a la aplicación

### 1. Mirar el modelo

Abrí [JDL Studio](https://start.jhipster.tech/jdl-studio/) y pegá el contenido de [`oncall.jh`](./oncall.jh). Vas a ver el diagrama entidad-relación completo: 13 entidades, 10 enumeraciones, y las relaciones entre ellas.

### 2. Generar

Desde la terminal del Dev Container:

```bash
jhipster jdl oncall.jh
```

Tomate el tiempo de mirar la consola. Cuando termine, contá los archivos:

```bash
git status --porcelain | wc -l
```

### 3. Levantar la base de datos

```bash
docker compose -f src/main/docker/postgresql.yml up -d
```

### 4. Levantar la aplicación

Dos terminales:

```bash
./mvnw          # backend, puerto 8080
```

```bash
npm start       # frontend, puerto 9000
```

Entrás en `http://localhost:9000` con `admin` / `admin`.

---

## Lo que el generador NO te dio

Recorré la aplicación. Podés dar de alta servicios, equipos, rotaciones, turnos, incidentes y alertas. Todo tiene ABM, validaciones, paginación, filtros, seguridad y tests.

Y sin embargo la aplicación **no sirve para nada todavía**, porque falta exactamente lo que ningún generador puede adivinar:

1. **Deduplicar alertas.** Llega una alerta con un `fingerprint` que ya existe en un incidente abierto: ¿se pega a ese incidente o abre uno nuevo?
2. **Resolver quién está de guardia ahora.** Dado un instante y una rotación, ¿qué persona es la responsable? ¿Y si hay un reemplazo cargado?
3. **Ejecutar el escalamiento.** Nadie reconoció el incidente en 5 minutos: hay que pasar al siguiente `PasoEscalamiento` y disparar la notificación.
4. **Calcular MTTA, MTTR y cumplimiento de SLO.** Con las marcas de tiempo del incidente y el `ObjetivoDeServicio` que aplica a su severidad.

Esas cuatro reglas son el material de las clases siguientes.

---

## Regenerar después de tocar el modelo

Si editás `oncall.jh` —agregás un campo, cambiás un tipo, sumás una entidad— **no alcanza con volver a correr `jhipster jdl`**. Hay dos estados que sobreviven a la regeneración y hay que limpiar a mano.

### 1. Regenerar el código

```bash
jhipster jdl oncall.jh --force
```

Sin `--force`, JHipster pregunta archivo por archivo si lo pisa.

Si cambiaste algo del bloque `application { config { ... } }` —el tema, el idioma, el tipo de base— además hay que borrar la memoria del generador, que vive en `.yo-rc.json`:

```bash
rm -rf .yo-rc.json .jhipster/
jhipster jdl oncall.jh --force
```

### 2. Recrear la base

Este paso es obligatorio y es el que más se olvida.

Liquibase guarda en la tabla `databasechangelog` un hash de cada changeset ejecutado. Al arrancar compara ese hash contra el archivo actual; si cambiaron, **frena antes de tocar nada** y la aplicación levanta con la base inutilizable:

```
Validation Failed: N changesets check sum
  ..._added_entity_Alerta.xml::...::jhipster was: 9:7d48... but is now: 9:3600...
```

No es un error a esquivar: es Liquibase evitando dejarte un esquema inconsistente. La solución es empezar de cero.

```bash
docker compose -f src/main/docker/postgresql.yml down -v
docker compose -f src/main/docker/postgresql.yml up -d
./mvnw
```

El `-v` es lo que importa: sin eso el volumen sobrevive y el error se repite.

**Si el error persiste**, el volumen no se borró — pasa cuando `down -v` se corre desde otro directorio, porque Compose deriva el nombre del proyecto de la carpeta. Vaciar el esquema directamente siempre funciona:

```bash
docker exec -it oncall-postgresql psql -U oncall -d oncall \
  -c "drop schema public cascade; create schema public;"
```

Eso se lleva las tablas **y** la `databasechangelog`, que es lo que realmente bloquea.

### 3. Verificar que arrancó limpio

En el log de arranque, buscá la línea de Liquibase:

```
Liquibase has updated your database in 4821 ms
```

Varios segundos significa que creó el esquema y cargó los datos falsos. **Un segundo o menos significa que no hizo nada**: encontró todo aplicado y siguió de largo. Ese es el síntoma de una base vieja.

Confirmación desde afuera:

```bash
docker exec -it oncall-postgresql psql -U oncall -d oncall -c "select count(*) from incidente;"
```

### Antes de commitear

Después de regenerar, **siempre**:

```bash
git status
```

Si aparecen cientos de archivos, la aplicación generada quedó sin ignorar. El repositorio versiona el modelo y el entorno, no el código generado.

---

## Estructura del repositorio

```
isa-oncall-monolithic/
├── .devcontainer/
│   └── devcontainer.json    # Java 21 + Node 22 + Docker + JHipster 9.2
├── docs/
│   └── modelo.md            # el modelo explicado en prosa
├── oncall.jh                # el modelo JDL
└── README.md
```

---

## Versiones

|             |                            |
| ----------- | -------------------------- |
| JHipster    | 9.2.0                      |
| Spring Boot | 4.0.x                      |
| Java        | 21 (LTS)                   |
| Angular     | 20.x                       |
| Node        | 22 (LTS)                   |
| PostgreSQL  | 17                         |
| Tests       | JUnit 5 · Vitest · Cypress |

---

## Si algo falla

**El Dev Container no arranca / se queda colgado.** Revisá que Docker Desktop esté corriendo y que tenga al menos 8 GB de memoria asignada (Settings → Resources).

**`jhipster jdl` falla con un error de parseo.** Confirmá la versión con `jhipster --version`. El JDL está escrito para 9.2.0.

**No querés depender de PostgreSQL.** Cambiá `devDatabaseType postgresql` por `devDatabaseType h2Disk` en el `oncall.jh` antes de generar. La aplicación levanta sin Docker, pero perdés el ejercicio de contenedores.

**El log está lleno de stack traces al arrancar.** Los `ProcessBuilder.start() debug` no son errores: son trazas de nivel DEBUG que emite el JDK al lanzar procesos externos, y las dispara el módulo de Docker Compose de Spring Boot buscando el binario de `docker`. Para silenciarlas, en `src/main/resources/config/application-dev.yml`:

```yaml
logging:
  level:
    ROOT: INFO

spring:
  docker:
    compose:
      enabled: false
```

---

### Dos problemas conocidos del generador

Los dos aparecieron generando este proyecto y **ya están corregidos en el `oncall.jh` del repositorio**. Se documentan porque son instructivos: el generador escribió más de quinientos archivos y dos de ellos vinieron mal.

**`Bad value for type long` al listar una entidad.** El campo era un `TextBlob`, que JHipster mapea a `@Lob String`. Contra PostgreSQL, Hibernate intenta leer esa columna como un _large object_ —identificado por un OID, o sea un número— pero la columna contiene texto, y la conversión falla. Se manifiesta al hacer `GET` sobre la entidad, no al arrancar, así que parece que los datos falsos no se cargaron cuando en realidad sí están.

> Solución: no usar `TextBlob` con PostgreSQL. Un `String maxlength(2000)` genera un `varchar` y se lee sin problema.

**`Could not resolve ".../bootswatch/dist/flatly||file:https://fonts.googleapis.com/..."`.** El build del frontend falla por un `@import` mal armado en `content/scss/vendor.scss`: una plantilla del generador concatenó la ruta del tema Bootswatch con la URL de la fuente en un solo `url()`. Aparece solo cuando se pide un `clientTheme`.

> Solución: quitar `clientTheme` del bloque `config`. Es puramente estético. Al regenerar hay que borrar `vendor.scss` y `.yo-rc.json`, porque si no el tema anterior queda recordado.
