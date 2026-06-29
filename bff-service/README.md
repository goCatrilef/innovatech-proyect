# bff-service

Backend For Frontend de la plataforma Innovatech.

## Descripción

`bff-service` es el componente encargado de adaptar y consolidar información para el frontend React.
Consume los microservicios a través del `api-gateway` y entrega respuestas optimizadas para la vista del usuario.

A diferencia del API Gateway, que solo enruta solicitudes, el BFF puede orquestar varias llamadas y construir una respuesta unificada.

## Tecnologías

* Java 21
* Spring Boot
* Spring Web
* Bean Validation
* Swagger/OpenAPI
* Lombok
* Eureka Discovery Client
* Spring Boot Actuator
* Maven

## Puerto

```txt
bff-service -> 8090
```

## Arquitectura

```txt
Frontend React
      |
      v
BFF :8090
      |
      v
API Gateway :8080
      |
      ├── ms-proyectos
      ├── ms-equipos
      └── ms-tareas
```

## Responsabilidad

El BFF permite que el frontend consuma una API más simple y adaptada a sus necesidades, evitando que React tenga que llamar directamente a varios microservicios.

## Endpoint principal

| Método | Endpoint                                  | Descripción                                   |
| ------ | ----------------------------------------- | --------------------------------------------- |
| GET    | `/api/bff/proyectos/{proyectoId}/resumen` | Obtiene un resumen consolidado de un proyecto |

## Endpoint consumido por el frontend

```txt
GET http://localhost:8090/api/bff/proyectos/1/resumen
```

## Respuesta esperada

```json
{
  "proyecto": {
    "id": 1,
    "codigo": "PRY-001",
    "nombre": "Sistema de Gestión de Proyectos",
    "descripcion": "Proyecto académico basado en microservicios",
    "fechaInicio": "2026-06-20",
    "fechaFin": "2026-12-20",
    "estado": "FINALIZADO"
  },
  "miembros": [],
  "tareas": [],
  "totalTareas": 1,
  "tareasPendientes": 0,
  "tareasEnProgreso": 0,
  "tareasFinalizadas": 1
}
```

## Configuración local

Archivo:

```txt
src/main/resources/application.yml
```

Ejemplo:

```yml
server:
  port: 8090

spring:
  application:
    name: bff-service

eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://localhost:8761/eureka/

app:
  api-gateway:
    url: http://localhost:8080
```

## Orden de ejecución

1. `eureka-server`
2. `ms-proyectos`
3. `ms-equipos`
4. `ms-tareas`
5. `api-gateway`
6. `bff-service`

## Ejecutar

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

## Swagger

```txt
http://localhost:8090/swagger-ui.html
```
## Decisiones técnicas

* El BFF no tiene base de datos propia.
* Consume el API Gateway en lugar de llamar directamente a los microservicios.
* Compone datos de proyectos, equipos y tareas.
* Entrega una respuesta consolidada para el frontend.
* Se registra en Eureka como parte de la arquitectura distribuida.

