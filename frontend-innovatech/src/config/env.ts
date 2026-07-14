type ViteEnvKey =
  | "VITE_BFF_URL"
  | "VITE_KEYCLOAK_URL"
  | "VITE_KEYCLOAK_REALM"
  | "VITE_KEYCLOAK_CLIENT_ID";

type RuntimeConfigKey =
  | "BFF_URL"
  | "KEYCLOAK_URL"
  | "KEYCLOAK_REALM"
  | "KEYCLOAK_CLIENT_ID";

type RuntimeConfig = Partial<Record<RuntimeConfigKey, string>>;

declare global {
  interface Window {
    __INNOVATECH_CONFIG__?: RuntimeConfig;
  }
}

function getRequiredConfig(
  runtimeKey: RuntimeConfigKey,
  viteKey: ViteEnvKey
): string {
  const runtimeValue =
    typeof window !== "undefined"
      ? window.__INNOVATECH_CONFIG__?.[runtimeKey]
      : undefined;

  const value = runtimeValue?.trim() || import.meta.env[viteKey]?.trim();

  if (!value) {
    throw new Error(
      `Falta configurar ${runtimeKey} (o su alternativa ${viteKey}).`
    );
  }

  return value;
}

function removeTrailingSlash(value: string): string {
  return value.replace(/\/+$/, "");
}

export const appConfig = Object.freeze({
  auth: {
    tokenMinValiditySeconds: 30,
  },
  bff: {
    baseUrl: removeTrailingSlash(
      getRequiredConfig("BFF_URL", "VITE_BFF_URL")
    ),
  },
  http: {
    timeoutMs: 10000,
  },
  keycloak: {
    clientId: getRequiredConfig(
      "KEYCLOAK_CLIENT_ID",
      "VITE_KEYCLOAK_CLIENT_ID"
    ),
    realm: getRequiredConfig("KEYCLOAK_REALM", "VITE_KEYCLOAK_REALM"),
    url: removeTrailingSlash(
      getRequiredConfig("KEYCLOAK_URL", "VITE_KEYCLOAK_URL")
    ),
  },
});
