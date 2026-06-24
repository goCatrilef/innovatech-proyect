type EnvKey =
  | "VITE_BFF_URL"
  | "VITE_KEYCLOAK_URL"
  | "VITE_KEYCLOAK_REALM"
  | "VITE_KEYCLOAK_CLIENT_ID";

function getRequiredEnv(key: EnvKey) {
  const value = import.meta.env[key];

  if (!value) {
    throw new Error(`Falta configurar la variable de entorno ${key}.`);
  }

  return value;
}

function removeTrailingSlash(value: string) {
  return value.replace(/\/$/, "");
}

export const appConfig = Object.freeze({
  auth: {
    tokenMinValiditySeconds: 30,
  },
  bff: {
    baseUrl: removeTrailingSlash(getRequiredEnv("VITE_BFF_URL")),
  },
  http: {
    timeoutMs: 10000,
  },
  keycloak: {
    clientId: getRequiredEnv("VITE_KEYCLOAK_CLIENT_ID"),
    realm: getRequiredEnv("VITE_KEYCLOAK_REALM"),
    url: removeTrailingSlash(getRequiredEnv("VITE_KEYCLOAK_URL")),
  },
});
