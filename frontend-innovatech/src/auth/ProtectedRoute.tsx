import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthProvider";

type ProtectedRouteProps = {
  children: ReactNode;
};

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { error, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-neutral-50">
        <p className="text-sm font-medium text-neutral-500">
          Cargando autenticación...
        </p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-neutral-50 px-4">
        <section className="max-w-md rounded-xl border border-danger-100 bg-white p-6 shadow-card">
          <p className="font-semibold text-danger-600">{error}</p>
          <p className="mt-2 text-sm text-neutral-500">
            Verifica que Keycloak esté disponible y que las variables de entorno
            estén configuradas correctamente.
          </p>
        </section>
      </main>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return children;
}
