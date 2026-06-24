import axios, { AxiosHeaders } from "axios";
import keycloak from "../auth/keycloak";
import { appConfig } from "../config/env";

export const bffApi = axios.create({
  baseURL: appConfig.bff.baseUrl,
  timeout: appConfig.http.timeoutMs,
});

bffApi.interceptors.request.use(async (config) => {
  if (keycloak.authenticated) {
    try {
      await keycloak.updateToken(appConfig.auth.tokenMinValiditySeconds);
    } catch {
      keycloak.clearToken();
      throw new Error(
        "La sesion expiro o no pudo renovarse. Inicia sesion nuevamente."
      );
    }

    if (keycloak.token) {
      config.headers = AxiosHeaders.from(config.headers);
      config.headers.set("Authorization", `Bearer ${keycloak.token}`);
    }
  }

  return config;
});
