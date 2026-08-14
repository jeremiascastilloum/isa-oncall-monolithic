# OnCall · Sistema de guardias e incidentes

Caso de estudio de **Ingeniería de Software Aplicada** — Universidad de Mendoza.

---

## El problema

Un equipo opera varios servicios en producción. Las herramientas de monitoreo tiran alertas todo el día: la mayoría es ruido, algunas son una interrupción real. Alguien tiene que estar de guardia, alguien tiene que ser despertado a las 3 de la mañana, y cuando el fuego se apaga hay que medir cuánto tardamos y escribir qué aprendimos.

Eso es lo que vamos a construir. No es casual: **es el mismo problema del que trata la materia**. Toda la complejidad que un framework nos permite delegar, después hay que operarla.

---

## Requisitos previos

Traer instalado y funcionando **antes** de la clase:

| Herramienta | Verificación |
|---|---|
| [Docker Desktop](https://www.docker.com/products/docker-desktop) | `docker --version` |
| [Git](https://git-scm.com/downloads) | `git --version` |
| [Visual Studio Code](https://code.visualstudio.com/download) | — |
| Extensión **Dev Containers** (`ms-vscode-remote.remote-containers`) | — |

> ⚠️ La primera vez que abras el Dev Container se descargan varios GB. **Hacelo en casa, no en la facultad.**

---

## Clase 1 · Levantar el entorno

El camino depende de tu sistema operativo. **En Windows no alcanza con clonar y abrir la carpeta** — leé la sección que te corresponde.

### Windows · cloná dentro de un volumen Docker

`Ctrl+Shift+P` → **Dev Containers: Clone Repository in Container Volume**, y pegá:

```
https://github.com/jeremiascastilloum/isa-oncall-monolithic.git
```

VS Code clona el repo adentro de un volumen de Docker y abre el contenedor apuntando ahí. No hace falta `git clone` previo ni `code .`.

**Por qué.** Si clonás a una carpeta de Windows (`C:\Users\...`) y hacés *Reopen in Container*, Docker Desktop expone esa carpeta sobre un filesystem llamado **9p**, que no soporta permisos de Linux: todos los archivos se ven como `root` y el usuario del contenedor no puede cambiarles el modo. Escribir archivos nuevos funciona, así que el entorno *parece* andar bien — hasta que en la Clase 2 el generador intenta sobrescribir un archivo existente, llama a `chmod` y muere con `EPERM: operation not permitted`.

De paso, el volumen es bastante más rápido: Maven, npm y Angular hacen mucha lectura y escritura de archivos chicos, y sobre 9p eso se arrastra.

**El costo:** el código vive adentro del volumen, no en tu disco de Windows. No lo vas a ver en el Explorador. Trabajás desde VS Code y corrés `git` desde la terminal del contenedor, que es lo que vas a hacer igual.

### Linux / macOS

```bash
git clone https://github.com/jeremiascastilloum/isa-oncall-monolithic.git
cd isa-oncall-monolithic
code .
```

Cuando VS Code muestre el aviso *"Folder contains a Dev Container configuration file"* → **Reopen in Container**.

Si no aparece: `Ctrl+Shift+P` → **Dev Containers: Reopen in Container**.

### Verificación

Al terminar, adentro del contenedor:

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

## Estructura del repositorio

```
isa-oncall-monolithic/
├── .devcontainer/
│   ├── devcontainer.json         # Java 21 + Node 22 + Docker + JHipster 9.2
│   ├── Dockerfile                # imagen base + parche del repo apt de Yarn
│   └── devcontainer-lock.json    # versiones fijas de los features
├── docs/
│   └── modelo.md            # el modelo explicado en prosa
├── oncall.jh                # el modelo JDL
└── README.md
```

---

## Versiones

| | |
|---|---|
| JHipster | 9.2.0 |
| Spring Boot | 4.0.x |
| Java | 21 (LTS) |
| Angular | 20.x |
| Node | 22 (LTS) |
| PostgreSQL | 17 |
| Tests | JUnit 5 · Vitest · Cypress |

---

## Si algo falla

**El Dev Container no arranca / se queda colgado.** Revisá que Docker Desktop esté corriendo y que tenga al menos 8 GB de memoria asignada (Settings → Resources).

**`jhipster jdl` falla con un error de parseo.** Confirmá la versión con `jhipster --version`. El JDL está escrito para 9.2.0.

**`jhipster jdl` genera cientos de archivos y después muere con `EPERM: operation not permitted, chmod`.** Estás en Windows con el repo clonado en una carpeta de Windows. Es lo que explica la [Clase 1](#windows--cloná-dentro-de-un-volumen-docker): volvé a clonar con **Clone Repository in Container Volume** y generá de nuevo desde ahí.

Si querés limpiar la generación a medias antes de rehacerla:

```bash
git checkout -- . && git clean -fd
```

Borra todo lo que el generador dejó. `oncall.jh` está versionado, así que no se pierde.

**No querés depender de PostgreSQL.** Cambiá `devDatabaseType postgresql` por `devDatabaseType h2Disk` en el `oncall.jh` antes de generar. La aplicación levanta sin Docker, pero perdés el ejercicio de contenedores.
