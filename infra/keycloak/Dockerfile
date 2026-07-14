FROM quay.io/keycloak/keycloak:26.0.0

LABEL org.opencontainers.image.title="Innovatech Keycloak" \
      org.opencontainers.image.description="Keycloak 26 con tema y realm base de Innovatech" \
      org.opencontainers.image.version="1.0.0"

COPY --chown=keycloak:keycloak themes/innovatech /opt/keycloak/themes/innovatech
COPY --chown=keycloak:keycloak realms/innovatech-realm.json /opt/keycloak/data/import/innovatech-realm.json
