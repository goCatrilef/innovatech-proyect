# ms-tareas

Microservicio encargado de la gestión de tareas asociadas a proyectos dentro de la plataforma Innovatech.

## Descripción

`ms-tareas` permite crear, consultar, actualizar, cambiar estado y asignar responsables a tareas.
Cada tarea debe estar asociada a un proyecto existente, validado mediante integración con `ms-proyectos`.

## Tecnologías

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Bean Validation
* Swagger/OpenAPI
* Lombok
* Maven
* Eureka Discovery Client

## Arquitectura

El microservicio usa arquitectura por capas:

```txt
controller  -> Endpoints REST
service     -> Reglas de negocio
repository  -> Acceso a datos
entity      -> Modelo de persistencia
dto         -> Datos de entrada y salida
mapper      -> Conversión entre entidad y DTO
exception   -> Manejo global de errores
client      -> Integración con otros microservicios
```

## Base de datos

Base independiente:

```txt
ms_tareas_db
```

## Estados de tarea

```txt
PENDING
IN_PROGRESS
DONE
```

Flujo permitido:

```txt
PENDING -> IN_PROGRESS -> DONE
```

## Reglas de negocio

* Toda tarea debe estar asociada a un proyecto existente.
* No se pueden crear tareas sin descripción.
* Toda tarea debe tener un responsable asignado.
* Una tarea nueva debe iniciar en estado `PENDING`.
* No se permiten transiciones inválidas de estado.

## Integración con ms-proyectos

`ms-tareas` valida la existencia del proyecto consultando:

```txt
GET http://localhost:8082/api/proyectos/{id}/existe
```

La comunicación se encapsula mediante una **Facade**:

```txt
ms-tareas -> ProyectoClientFacade -> ms-proyectos
```

## Endpoints principales

| Método | Endpoint                                  | Descripción                      |
| ------ | ----------------------------------------- | -------------------------------- |
| POST   | `/api/tareas`                             | Crear tarea                      |
| GET    | `/api/tareas/{id}`                        | Buscar tarea por ID              |
| GET    | `/api/tareas/proyecto/{proyectoId}`       | Consultar tareas por proyecto    |
| GET    | `/api/tareas/estado/{estado}`             | Consultar tareas por estado      |
| GET    | `/api/tareas/responsable/{responsableId}` | Consultar tareas por responsable |
| PUT    | `/api/tareas/{id}`                        | Actualizar tarea                 |
| PATCH  | `/api/tareas/{id}/estado`                 | Cambiar estado                   |
| PATCH  | `/api/tareas/{id}/responsable`            | Asignar responsable              |

## Ejemplo de creación

```json
{
  "descripcion": "Implementar endpoint de tareas",
  "proyectoId": 1,
  "responsableId": 1,
  "estado": "PENDING"
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
  port: 8083

spring:
  application:
    name: ms-tareas

  datasource:
    url: jdbc:postgresql://localhost:5433/ms_tareas_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

app:
  proyectos-service:
    url: http://localhost:8082
```

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
http://localhost:8083/swagger-ui.html
```

## Health Check

```txt
http://localhost:8083/actuator/health
```

## Decisiones técnicas

* Se usan DTOs para no exponer entidades JPA.
* Se utiliza una base de datos propia para mantener autonomía.
* Se usa un enum para controlar estados válidos.
* Se aplica Facade para integrar con `ms-proyectos`.
* Se evita compartir entidades entre microservicios usando `proyectoId` y `responsableId`.

## Defensa breve

`ms-tareas` gestiona el trabajo operativo de los proyectos. Es independiente, tiene su propia base de datos y valida la existencia del proyecto mediante una Facade hacia `ms-proyectos`, manteniendo bajo acoplamiento entre microservicios.
