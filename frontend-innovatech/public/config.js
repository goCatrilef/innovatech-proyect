// Valores para desarrollo local con `pnpm dev`.
// En Docker este archivo se reemplaza al iniciar el contenedor.
window.__INNOVATECH_CONFIG__ = Object.freeze({
  BFF_URL: "http://localhost:8090",
  KEYCLOAK_URL: "http://localhost:8085",
  KEYCLOAK_REALM: "innovatech",
  KEYCLOAK_CLIENT_ID: "innovatech-frontend"
});
