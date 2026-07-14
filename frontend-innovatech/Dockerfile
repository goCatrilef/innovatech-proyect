# syntax=docker/dockerfile:1.7

# -------------------------------------------------------
# Etapa 1: compilación del frontend React/Vite
# -------------------------------------------------------
FROM node:22-alpine AS build

WORKDIR /app

RUN corepack enable

COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

COPY . .
RUN pnpm build

# -------------------------------------------------------
# Etapa 2: ejecución con Nginx sin privilegios
# -------------------------------------------------------
FROM nginxinc/nginx-unprivileged:1.27-alpine AS runtime

USER root

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=101:101 /app/dist /usr/share/nginx/html
COPY --chown=101:101 docker-entrypoint.d/40-generate-runtime-config.sh /docker-entrypoint.d/40-generate-runtime-config.sh

RUN chmod +x /docker-entrypoint.d/40-generate-runtime-config.sh \
    && chown -R 101:101 /usr/share/nginx/html /etc/nginx/conf.d/default.conf

USER 101

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=5 \
  CMD wget -qO- http://127.0.0.1:8080/healthz || exit 1
