# ms-equipos

Microservicio REST para gestionar miembros de equipos y asignarlos a proyectos.

## Stack

- Java 21
- Spring Boot 3.5.15
- Spring Web / Data JPA / Validation
- PostgreSQL
- Lombok
- Swagger OpenAPI
- H2 para tests

## Configuracion

Archivo principal: `src/main/resources/application.yml`

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/ms_equipos_db
    username: postgres
    password: postgres
```

Crear la base de datos antes de levantar el servicio:

```sql
CREATE DATABASE ms_equipos_db;
```

## Ejecutar

```bash
mvn spring-boot:run
```

API disponible en:

```text
http://localhost:8081
```

## Tests

```bash
mvn test
```

## Swagger

```text
http://localhost:8081/swagger-ui.html
```

## Endpoints

Base path: `/api/equipos`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/miembros` | Crear miembro |
| GET | `/miembros` | Listar miembros |
| GET | `/miembros/{id}` | Obtener miembro |
| PUT | `/miembros/{id}` | Actualizar miembro |
| PATCH | `/miembros/{id}/desactivar` | Desactivar miembro |
| POST | `/asignaciones` | Asignar miembro a proyecto |
| GET | `/proyectos/{proyectoId}/miembros` | Listar miembros por proyecto |

## Ejemplo

```http
POST http://localhost:8081/api/equipos/miembros
Content-Type: application/json

{
  "identificador": "USR-001",
  "nombre": "Gonzalo Perez",
  "rol": "Desarrollador Backend",
  "email": "gonzalo.perez@example.com",
  "activo": true
}
```

## Validaciones principales

- `identificador`, `nombre` y `rol` son obligatorios.
- `email` es opcional, pero debe tener formato valido si se envia.
- `identificador` debe ser unico.
- Un miembro inactivo no puede asignarse a proyectos.
- No se puede repetir la misma asignacion miembro-proyecto.

## Health check

```text
http://localhost:8081/actuator/health
```
