import Keycloak from "keycloak-js";
import { appConfig } from "../config/env";

const keycloak = new Keycloak(appConfig.keycloak);

export default keycloak;
