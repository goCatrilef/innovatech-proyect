import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import keycloak from "./keycloak";

type AuthContextValue = {
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);
const AUTH_INITIALIZATION_TIMEOUT_MS = 10000;

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    const initializeAuth = async () => {
      const timeout = new Promise<never>((_, reject) => {
        window.setTimeout(() => {
          reject(
            new Error(
              "Keycloak tardo demasiado en responder. Verifica que el servicio este activo."
            )
          );
        }, AUTH_INITIALIZATION_TIMEOUT_MS);
      });

      return Promise.race([
        keycloak.init({
          onLoad: "login-required",
          checkLoginIframe: false,
        }),
        timeout,
      ]);
    };

    initializeAuth()
      .then((authenticated) => {
        if (isMounted) {
          setIsAuthenticated(authenticated);
        }
      })
      .catch((cause) => {
        if (isMounted) {
          const message =
            cause instanceof Error
              ? cause.message
              : "No fue posible iniciar sesion con Keycloak.";

          setError(message);
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      isAuthenticated,
      isLoading,
      error,
      logout: () => {
        keycloak.logout({
          redirectUri: window.location.origin,
        });
      },
    }),
    [error, isAuthenticated, isLoading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth debe usarse dentro de AuthProvider.");
  }

  return context;
}
