# Innovatech Platform

Plataforma para la gestión de proyectos construida con arquitectura de microservicios.

## Descripción

Innovatech permite gestionar proyectos, equipos y tareas mediante una arquitectura distribuida basada en Spring Boot, Eureka, API Gateway, BFF, Keycloak y Frontend React.

El sistema separa responsabilidades por microservicio y centraliza el acceso mediante Gateway y BFF.

## Arquitectura general

```txt
Frontend React :5173
        |
        | JWT Keycloak
        v
BFF Service :8090
        |
        v
API Gateway :8080
        |
        ├── ms-equipos    :8081
        ├── ms-proyectos  :8082
        └── ms-tareas     :8083

Eureka Server :8761
Keycloak      :8085
PostgreSQL
```

## Componentes

| Componente            | Puerto | Responsabilidad                    |
| --------------------- | -----: | ---------------------------------- |
| `frontend-innovatech` |   5173 | Interfaz web                       |
| `bff-service`         |   8090 | Adaptación de datos para frontend  |
| `api-gateway`         |   8080 | Enrutamiento centralizado          |
| `eureka-server`       |   8761 | Service discovery                  |
| `ms-equipos`          |   8081 | Gestión de miembros y asignaciones |
| `ms-proyectos`        |   8082 | Gestión de proyectos               |
| `ms-tareas`           |   8083 | Gestión de tareas                  |
| `keycloak`            |   8085 | Autenticación y autorización       |

## Microservicios

### ms-equipos

Permite registrar miembros, consultarlos y asociarlos a proyectos.

### ms-proyectos

Permite registrar, consultar, actualizar y cambiar estado de proyectos.

### ms-tareas

Permite crear tareas, consultar tareas por proyecto, cambiar estado y asignar responsables.

## Seguridad

La seguridad se implementa con Keycloak.

* Realm: `innovatech`
* Cliente: `innovatech-frontend`
* Roles: `ADMIN`, `PROJECT_MANAGER`, `DEVELOPER`, `USER`
* El frontend obtiene un JWT.
* El BFF valida el token antes de permitir acceso a endpoints protegidos.

## Bases de datos

Cada microservicio tiene su propia base de datos:

```txt
ms_equipos_db
ms_proyectos_db
ms_tareas_db
```

Esta decisión mantiene autonomía de datos y bajo acoplamiento.

## Ejecución recomendada

Levantar los componentes en este orden:

```txt
1. Keycloak
2. Eureka Server
3. ms-proyectos
4. ms-equipos
5. ms-tareas
6. API Gateway
7. BFF Service
8. Frontend React
```

## Comandos backend

En cada servicio Spring Boot:

```bash
mvn clean install
mvn spring-boot:run
```

## Comandos frontend

```bash
pnpm install
pnpm dev
```

## Endpoints principales

### BFF

```txt
GET /api/bff/proyectos/{id}/resumen
```

### API Gateway

```txt
/api/proyectos/**
/api/equipos/**
/api/tareas/**
```

## Decisiones técnicas

* Arquitectura basada en microservicios.
* Base de datos independiente por servicio.
* Eureka para descubrimiento de servicios.
* API Gateway como punto de entrada técnico.
* BFF para adaptar respuestas al frontend.
* Keycloak para autenticación y autorización.
* React con pnpm para la interfaz web.
* DTOs y validaciones para proteger contratos de entrada y salida.
