import axios from "axios";
import keycloak from "../auth/keycloak";

export const bffApi = axios.create({
  baseURL: import.meta.env.VITE_BFF_URL,
});

bffApi.interceptors.request.use(async (config) => {
  if (keycloak.authenticated) {
    await keycloak.updateToken(30);

    if (keycloak.token) {
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
  }

  return config;
});