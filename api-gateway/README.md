# api-gateway

API Gateway de la plataforma Innovatech. Actúa como punto de entrada único para enrutar solicitudes hacia los microservicios registrados en Eureka.

## Descripción

`api-gateway` centraliza el acceso a los microservicios del sistema. Permite que los clientes consuman una sola URL base en lugar de llamar directamente a cada microservicio por su puerto interno.

## Tecnologías

* Java 21
* Spring Boot
* Spring Cloud Gateway Web MVC
* Eureka Discovery Client
* Spring Boot Actuator
* Maven

## Puerto

```txt
api-gateway -> 8080
```

## Arquitectura

```txt
Cliente / Frontend / BFF
        |
        v
API Gateway :8080
        |
        ├── /api/equipos/**    -> ms-equipos
        ├── /api/proyectos/**  -> ms-proyectos
        └── /api/tareas/**     -> ms-tareas
```

## Integración con Eureka

El Gateway se registra en Eureka y enruta hacia los microservicios usando sus nombres registrados:

```txt
MS-EQUIPOS
MS-PROYECTOS
MS-TAREAS
```

Esto evita depender directamente de puertos internos como `8081`, `8082` o `8083`.

## Rutas principales

| Ruta Gateway        | Microservicio destino |
| ------------------- | --------------------- |
| `/api/equipos/**`   | `ms-equipos`          |
| `/api/proyectos/**` | `ms-proyectos`        |
| `/api/tareas/**`    | `ms-tareas`           |

## Configuración local

Archivo:

```txt
src/main/resources/application.yml
```

Configuración principal:

```yml
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      server:
        webmvc:
          routes:
            - id: ms-proyectos
              uri: lb://MS-PROYECTOS
              predicates:
                - Path=/api/proyectos,/api/proyectos/**

            - id: ms-equipos
              uri: lb://MS-EQUIPOS
              predicates:
                - Path=/api/equipos,/api/equipos/**

            - id: ms-tareas
              uri: lb://MS-TAREAS
              predicates:
                - Path=/api/tareas,/api/tareas/**
```

## Orden de ejecución

1. Levantar Eureka Server.
2. Levantar los microservicios.
3. Levantar API Gateway.

```bash
mvn spring-boot:run
```

## Pruebas

```http
GET http://localhost:8080/api/proyectos
GET http://localhost:8080/api/equipos/miembros
GET http://localhost:8080/api/tareas/estado/PENDING
```

## Decisiones técnicas

* El Gateway centraliza el acceso a los microservicios.
* Usa Eureka para descubrir servicios por nombre.
* Evita que el cliente conozca los puertos internos.
* Facilita agregar seguridad, filtros, logs y control de tráfico en un solo punto.

