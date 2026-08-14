ARG VARIANT=1-21-bookworm
FROM mcr.microsoft.com/devcontainers/java:${VARIANT}

# La imagen base registra el repo apt de Yarn, pero su keyring quedó desfasado
# respecto a la clave con la que Yarn firma hoy (NO_PUBKEY 62D54FD4003F6525).
# Eso hace fallar `apt-get update` dentro de los features (docker-in-docker).
# El binario de yarn ya viene instalado, así que basta con retirar el repo.
RUN rm -f /etc/apt/sources.list.d/yarn.list \
    && apt-get update \
    && rm -rf /var/lib/apt/lists/*
