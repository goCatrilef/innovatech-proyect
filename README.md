# Frontend Innovatech

Frontend web de la plataforma Innovatech, desarrollado con React, TypeScript, pnpm y Tailwind CSS.

## Descripción

Este frontend permite interactuar con la plataforma de gestión de proyectos, tareas y equipos.
Se autentica mediante Keycloak, consume el BFF y muestra información consolidada del sistema.

## Tecnologías

* React
* TypeScript
* Vite
* pnpm
* Tailwind CSS
* Axios
* React Router DOM
* Keycloak JS

## Funcionalidades

* Login con Keycloak.
* Visualización de proyectos.
* Creación de proyectos.
* Gestión de tareas por proyecto.
* Visualización de avance de tareas.
* Consulta de miembros del equipo.
* Asociación de miembros a proyectos.
* Manejo de errores del backend.
* Validación de formularios obligatorios.

## Arquitectura de consumo

```txt
Frontend React
      |
      | JWT Keycloak
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

## Variables de entorno

Archivo:

```txt
.env
```

Ejemplo:

```env
VITE_KEYCLOAK_URL=http://localhost:8085
VITE_KEYCLOAK_REALM=innovatech
VITE_KEYCLOAK_CLIENT_ID=innovatech-frontend

VITE_BFF_URL=http://localhost:8090
```

## Instalación

```bash
pnpm install
```

## Ejecución

```bash
pnpm dev
```

URL local:

```txt
http://localhost:5173
```

## Decisiones técnicas

* El frontend consume el BFF, no los microservicios directamente.
* Keycloak gestiona autenticación y emisión de tokens JWT.
* Axios envía el token JWT en cada solicitud protegida.
* Se utilizan componentes reutilizables para formularios, tarjetas, vistas y mensajes de error.
* Se validan formularios antes de enviar datos al backend.

## Defensa breve

El frontend fue desarrollado con React y pnpm, integrando autenticación con Keycloak y consumo del BFF. Permite gestionar proyectos, tareas y miembros mediante una interfaz web, manteniendo bajo acoplamiento con los microservicios internos.
