# Seguridad con Keycloak

Configuración de seguridad para la plataforma Innovatech usando Keycloak como proveedor de identidad.

## Descripción

Keycloak se utiliza para gestionar autenticación y autorización de usuarios mediante tokens JWT.
El frontend obtiene un token desde Keycloak y luego lo envía al `bff-service`, que valida el token antes de permitir el acceso a los endpoints protegidos.

## Arquitectura de seguridad

```txt
Frontend React
      |
      | JWT Bearer Token
      v
BFF Service :8090
      |
      v
API Gateway :8080
      |
      ├── ms-proyectos
      ├── ms-equipos
      └── ms-tareas
```

## Puerto de Keycloak

```txt
Keycloak -> http://localhost:8085
```

## Realm

Se creó el realm:

```txt
innovatech
```

El realm permite aislar la configuración de seguridad de la plataforma, incluyendo usuarios, roles y clientes.

## Cliente configurado

Cliente utilizado:

```txt
innovatech-frontend
```

Configuración principal:

```txt
Client authentication: OFF
Standard flow: ON
Direct access grants: ON
```

Para desarrollo local:

```txt
Valid redirect URIs:
http://localhost:5173/*

Web origins:
http://localhost:5173
```

## Roles

Roles definidos en el realm:

```txt
ADMIN
PROJECT_MANAGER
DEVELOPER
USER
```

Uso esperado:

```txt
ADMIN            -> Administración general
PROJECT_MANAGER  -> Gestión de proyectos
DEVELOPER        -> Participación en tareas
USER             -> Usuario base del sistema
```

## Usuario de prueba

Usuario utilizado para pruebas:

```txt
Username: test.admin
Email: test.admin@innovatech.cl
Password: Admin123
Roles: ADMIN, USER
```

Configuración importante del usuario:

```txt
Enabled: ON
Email verified: ON
Required user actions: vacío
Temporary password: OFF
```

### 
```

## Configuración del BFF

El `bff-service` se configura como OAuth2 Resource Server para validar tokens emitidos por Keycloak.

Archivo:

```txt
bff-service/src/main/resources/application.yml
```

Configuración principal:

```yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8085/realms/innovatech
```

## Dependencias usadas en el BFF

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

## Autorización por roles

Keycloak envía los roles dentro del token en:

```txt
realm_access.roles
```

El BFF transforma esos roles al formato usado por Spring Security:

```txt
ROLE_ADMIN
ROLE_USER
ROLE_DEVELOPER
ROLE_PROJECT_MANAGER
```

## Decisiones técnicas

* Keycloak se usa como proveedor de identidad.
* El frontend obtiene un JWT desde Keycloak.
* El BFF valida el token como Resource Server.
* Se protege el BFF porque es el punto de entrada especializado para el frontend.
* Los roles permiten controlar permisos según el perfil del usuario.
* El API Gateway mantiene su responsabilidad principal de enrutar solicitudes.
