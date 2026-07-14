-- Bases lógicas para Innovatech.
-- El script se ejecuta solamente al inicializar un volumen PostgreSQL vacío.

SELECT 'CREATE DATABASE ms_equipos_db OWNER ' || quote_ident(current_user)
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ms_equipos_db')\gexec

SELECT 'CREATE DATABASE ms_proyectos_db OWNER ' || quote_ident(current_user)
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ms_proyectos_db')\gexec

SELECT 'CREATE DATABASE ms_tareas_db OWNER ' || quote_ident(current_user)
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ms_tareas_db')\gexec

SELECT 'CREATE DATABASE keycloak_db OWNER ' || quote_ident(current_user)
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak_db')\gexec
