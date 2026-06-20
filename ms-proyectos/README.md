# ms-proyectos

Microservicio de gestión de proyectos tecnológicos para la plataforma académica de gestión de proyectos de Innovatech.

## Descripción

`ms-proyectos` permite registrar, consultar, actualizar y cambiar el estado de proyectos tecnológicos. Además, expone un endpoint para validar si un proyecto existe, lo que permite la integración con otros microservicios como `ms-equipos`.

Este microservicio forma parte de una arquitectura basada en microservicios que luego se integrará con Eureka, API Gateway, Keycloak, BFF y frontend React.

## Tecnologías

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Bean Validation
* Swagger/OpenAPI
* Lombok
* JUnit 5
* Mockito
* Maven

## Arquitectura

El microservicio usa arquitectura por capas:

```txt
controller  -> Expone endpoints REST
service     -> Contiene reglas de negocio
repository  -> Acceso a datos
entity      -> Modelo de persistencia
dto         -> Entrada y salida de datos
mapper      -> Conversión entre entidades y DTOs
exception   -> Manejo global de errores
```

## Base de datos

Base de datos independiente:

```txt
ms_proyectos_db
```

Esto permite mantener autonomía de datos y evitar acoplamiento con otros microservicios.

## Entidad principal

### Proyecto

Representa un proyecto tecnológico dentro del sistema.

Campos principales:

* `id`
* `codigo`
* `nombre`
* `descripcion`
* `fechaInicio`
* `fechaFin`
* `estado`
* `fechaCreacion`

## Estados del proyecto

Los estados válidos son:

```txt
PLANIFICADO
EN_PROGRESO
FINALIZADO
CANCELADO
```

Se utiliza un `enum` para evitar estados inválidos o mal escritos.

## Reglas de negocio

* Cada proyecto debe tener un código único.
* No se pueden registrar proyectos sin código, nombre o fecha de inicio.
* No se permiten proyectos duplicados con el mismo código.
* La fecha de término no puede ser anterior a la fecha de inicio.
* El estado del proyecto debe pertenecer a los valores definidos.
* Otros microservicios pueden validar si un proyecto existe mediante un endpoint específico.

## Endpoints principales

| Método | Endpoint                     | Descripción                   |
| ------ | ---------------------------- | ----------------------------- |
| POST   | `/api/proyectos`             | Registrar proyecto            |
| GET    | `/api/proyectos`             | Listar proyectos              |
| GET    | `/api/proyectos/{id}`        | Buscar proyecto por ID        |
| PUT    | `/api/proyectos/{id}`        | Actualizar proyecto           |
| PATCH  | `/api/proyectos/{id}/estado` | Cambiar estado del proyecto   |
| GET    | `/api/proyectos/{id}/existe` | Validar si un proyecto existe |

## Ejemplo de registro

```json
{
  "codigo": "PRY-001",
  "nombre": "Sistema de Gestión de Proyectos",
  "descripcion": "Proyecto académico basado en microservicios",
  "fechaInicio": "2026-06-17",
  "fechaFin": "2026-12-20",
  "estado": "PLANIFICADO"
}
```

## Ejemplo de cambio de estado

```json
{
  "estado": "EN_PROGRESO"
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
  port: 8082

spring:
  application:
    name: ms-proyectos

  datasource:
    url: jdbc:postgresql://localhost:5433/ms_proyectos_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

> Nota: el puerto de PostgreSQL puede ser `5432` o `5433`, según la instalación local.

## Ejecutar el proyecto

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

Ejecutar pruebas:

```bash
mvn test
```

## Swagger

Con el microservicio levantado:

```txt
http://localhost:8082/swagger-ui.html
```

## Health Check

```txt
http://localhost:8082/actuator/health
```

Respuesta esperada:

```json
{
  "status": "UP"
}
```

## Decisiones técnicas

* Se usan DTOs para no exponer entidades JPA directamente.
* Se utiliza PostgreSQL como base de datos propia del microservicio.
* Se usa un `enum` para controlar los estados válidos del proyecto.
* Se implementa manejo global de errores para respuestas claras y consistentes.
* Se expone un endpoint `/api/proyectos/{id}/existe` para integración con otros microservicios.

