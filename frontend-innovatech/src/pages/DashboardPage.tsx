import { useState } from "react";
import { bffApi } from "../api/bffApi";
import type { ProjectSummary } from "../types/project";

type MetricCardProps = {
  label: string;
  value: number;
};

function MetricCard({ label, value }: MetricCardProps) {
  return (
    <article className="rounded-xl border border-neutral-200 bg-neutral-50 p-4">
      <p className="text-sm text-neutral-500">{label}</p>
      <p className="mt-1 text-3xl font-bold text-neutral-900">{value}</p>
    </article>
  );
}

export function DashboardPage() {
  const [summary, setSummary] = useState<ProjectSummary | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const loadSummary = async () => {
    try {
      setError("");
      setIsLoading(true);

      const response = await bffApi.get<ProjectSummary>(
        "/api/bff/proyectos/1/resumen"
      );

      setSummary(response.data);
    } catch {
      setError("No fue posible obtener el resumen del proyecto.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <section className="rounded-xl border border-neutral-200 bg-white p-6 shadow-card">
        <header className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-wide text-accent-600">
              Conexión inicial
            </p>
            <p className="mt-2 text-neutral-500">
              Valida que el frontend pueda consultar el resumen desde el BFF.
            </p>
          </div>
        </header>

        <div className="mt-6 flex flex-col gap-3 sm:flex-row sm:items-center">
          <button
            type="button"
            onClick={loadSummary}
            disabled={isLoading}
            className="rounded-lg bg-primary-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isLoading ? "Cargando..." : "Cargar resumen del proyecto"}
          </button>

          {error && <p className="text-sm font-medium text-danger-600">{error}</p>}
        </div>

        {summary ? (
          <section className="mt-8 rounded-xl border border-neutral-200 p-6">
            <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <h2 className="text-xl font-bold text-neutral-900">
                  {summary.proyecto.nombre}
                </h2>
                <p className="mt-1 text-neutral-500">
                  Código: {summary.proyecto.codigo}
                </p>
              </div>

              <span className="w-fit rounded-full bg-accent-100 px-3 py-1 text-xs font-semibold text-accent-600">
                {summary.proyecto.estado}
              </span>
            </div>

            <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <MetricCard label="Total tareas" value={summary.totalTareas} />
              <MetricCard
                label="Pendientes"
                value={summary.tareasPendientes}
              />
              <MetricCard
                label="En progreso"
                value={summary.tareasEnProgreso}
              />
              <MetricCard
                label="Finalizadas"
                value={summary.tareasFinalizadas}
              />
            </div>
          </section>
        ) : (
          <section className="mt-8 rounded-xl border border-dashed border-neutral-300 bg-neutral-50 p-8 text-center">
            <h2 className="text-lg font-semibold text-neutral-700">
              Aún no hay datos cargados
            </h2>
            <p className="mt-2 text-sm text-neutral-500">
              Usa el botón para validar la conexión entre frontend, Keycloak y
              BFF.
            </p>
          </section>
        )}
      </section>
    </div>
  );
}
