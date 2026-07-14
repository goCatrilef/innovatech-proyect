#!/bin/sh
set -eu

escape_js() {
  printf '%s' "$1" | sed 's/\\/\\\\/g; s/"/\\"/g'
}

BFF_URL="$(escape_js "${BFF_PUBLIC_URL:-http://localhost:8090}")"
KC_URL="$(escape_js "${KEYCLOAK_PUBLIC_URL:-http://localhost:8085}")"
KC_REALM="$(escape_js "${KEYCLOAK_REALM:-innovatech}")"
KC_CLIENT="$(escape_js "${KEYCLOAK_CLIENT_ID:-innovatech-frontend}")"

cat > /usr/share/nginx/html/config.js <<EOF_CONFIG
window.__INNOVATECH_CONFIG__ = Object.freeze({
  BFF_URL: "${BFF_URL}",
  KEYCLOAK_URL: "${KC_URL}",
  KEYCLOAK_REALM: "${KC_REALM}",
  KEYCLOAK_CLIENT_ID: "${KC_CLIENT}"
});
EOF_CONFIG

echo "Configuración pública de Innovatech generada correctamente."
