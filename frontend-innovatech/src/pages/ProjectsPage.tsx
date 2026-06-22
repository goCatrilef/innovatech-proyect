import { useEffect, useMemo, useState, type FormEvent } from "react";
import { getApiErrorMessage, getApiErrorStatus } from "../api/apiError";
import { createProject, getProjects } from "../api/projectApi";
import type {
  CreateProjectRequest,
  Project,
  ProjectStatus,
} from "../types/project";

type ProjectFormState = {
  codigo: string;
  nombre: string;
  descripcion: string;
  fechaInicio: string;
  fechaFin: string;
  estado: ProjectStatus;
};

type ProjectFormErrors = Partial<Record<keyof ProjectFormState, string>>;

const initialFormState: ProjectFormState = {
  codigo: "",
  nombre: "",
  descripcion: "",
  fechaInicio: "",
  fechaFin: "",
  estado: "PLANIFICADO",
};

const projectStatuses: ProjectStatus[] = [
  "PLANIFICADO",
  "EN_PROGRESO",
  "FINALIZADO",
  "CANCELADO",
];

const statusLabels: Record<ProjectStatus, string> = {
  PLANIFICADO: "Planificado",
  EN_PROGRESO: "En progreso",
  FINALIZADO: "Finalizado",
  CANCELADO: "Cancelado",
};

const statusStyles: Record<ProjectStatus, string> = {
  PLANIFICADO: "bg-blue-50 text-blue-700 ring-blue-100",
  EN_PROGRESO: "bg-cyan-50 text-cyan-700 ring-cyan-100",
  FINALIZADO: "bg-emerald-50 text-emerald-700 ring-emerald-100",
  CANCELADO: "bg-red-50 text-red-700 ring-red-100",
};

const isoDatePattern = /^\d{4}-\d{2}-\d{2}$/;

function isValidIsoDate(date: string) {
  if (!isoDatePattern.test(date)) {
    return false;
  }

  const parsedDate = new Date(`${date}T00:00:00`);
  return !Number.isNaN(parsedDate.getTime());
}

function validateProjectForm(form: ProjectFormState) {
  const errors: ProjectFormErrors = {};

  if (!form.codigo.trim()) {
    errors.codigo = "El codigo es obligatorio.";
  }

  if (!form.nombre.trim()) {
    errors.nombre = "El nombre es obligatorio.";
  }

  if (!form.fechaInicio) {
    errors.fechaInicio = "La fecha de inicio es obligatoria.";
  } else if (!isValidIsoDate(form.fechaInicio)) {
    errors.fechaInicio = "Usa el formato YYYY-MM-DD.";
  }

  if (form.fechaFin && !isValidIsoDate(form.fechaFin)) {
    errors.fechaFin = "Usa el formato YYYY-MM-DD.";
  }

  if (
    isValidIsoDate(form.fechaInicio) &&
    form.fechaFin &&
    isValidIsoDate(form.fechaFin) &&
    new Date(form.fechaFin) < new Date(form.fechaInicio)
  ) {
    errors.fechaFin = "La fecha de fin no puede ser anterior al inicio.";
  }

  return errors;
}

function formatDate(date?: string | null) {
  if (!date) {
    return "Sin fecha";
  }

  return new Intl.DateTimeFormat("es-CL", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(new Date(`${date}T00:00:00`));
}

function toCreateProjectRequest(form: ProjectFormState): CreateProjectRequest {
  return {
    codigo: form.codigo.trim(),
    nombre: form.nombre.trim(),
    descripcion: form.descripcion.trim() || undefined,
    fechaInicio: form.fechaInicio,
    fechaFin: form.fechaFin || undefined,
    estado: form.estado,
  };
}

export function ProjectsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [form, setForm] = useState<ProjectFormState>(initialFormState);
  const [formErrors, setFormErrors] = useState<ProjectFormErrors>({});
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const totalActiveProjects = useMemo(
    () => projects.filter((project) => project.estado !== "CANCELADO").length,
    [projects]
  );

  const loadProjects = async () => {
    try {
      setError("");
      setIsLoading(true);
      const data = await getProjects();
      setProjects(data);
    } catch (cause) {
      if (getApiErrorStatus(cause) === 404) {
        setError(
          "El BFF aun no expone GET /api/bff/proyectos. El frontend esta preparado, pero falta publicar ese contrato en backend."
        );
      } else {
        setError(
          getApiErrorMessage(
            cause,
            "No fue posible cargar los proyectos desde el BFF."
          )
        );
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void loadProjects();
  }, []);

  const updateField = (field: keyof ProjectFormState, value: string) => {
    setForm((currentForm) => ({
      ...currentForm,
      [field]: value,
    }));

    setFormErrors((currentErrors) => ({
      ...currentErrors,
      [field]: undefined,
    }));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const errors = validateProjectForm(form);
    setFormErrors(errors);
    setSuccessMessage("");

    if (Object.keys(errors).length > 0) {
      return;
    }

    try {
      setError("");
      setIsSubmitting(true);

      const createdProject = await createProject(toCreateProjectRequest(form));

      setProjects((currentProjects) => [createdProject, ...currentProjects]);
      setForm(initialFormState);
      setSuccessMessage("Proyecto creado correctamente.");
    } catch (cause) {
      if (getApiErrorStatus(cause) === 404) {
        setError(
          "El BFF aun no expone POST /api/bff/proyectos. No se llamo directo al microservicio para respetar la arquitectura."
        );
      } else {
        setError(
          getApiErrorMessage(
            cause,
            "No fue posible crear el proyecto desde el BFF."
          )
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_380px]">
      <section className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-3">
          <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm font-medium text-slate-500">
              Total proyectos
            </p>
            <p className="mt-2 text-3xl font-bold text-slate-950">
              {projects.length}
            </p>
          </article>
          <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm font-medium text-slate-500">
              Proyectos activos
            </p>
            <p className="mt-2 text-3xl font-bold text-slate-950">
              {totalActiveProjects}
            </p>
          </article>
          <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm font-medium text-slate-500">
              Fuente de datos
            </p>
            <p className="mt-2 text-lg font-bold text-cyan-700">BFF</p>
          </article>
        </div>

        <section className="rounded-xl border border-slate-200 bg-white shadow-sm">
          <div className="flex flex-col gap-3 border-b border-slate-100 p-5 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-xl font-bold text-slate-950">
                Proyectos registrados
              </h2>
              <p className="mt-1 text-sm text-slate-500">
                Listado obtenido desde la capa BFF de Innovatech.
              </p>
            </div>

            <button
              type="button"
              onClick={loadProjects}
              disabled={isLoading}
              className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isLoading ? "Actualizando..." : "Actualizar"}
            </button>
          </div>

          {error && (
            <div className="m-5 rounded-lg border border-red-100 bg-red-50 p-4 text-sm text-red-700">
              {error}
            </div>
          )}

          {isLoading ? (
            <div className="p-5">
              <div className="h-32 animate-pulse rounded-lg bg-slate-100" />
            </div>
          ) : projects.length === 0 ? (
            <div className="p-8 text-center">
              <h3 className="text-lg font-semibold text-slate-800">
                No hay proyectos para mostrar
              </h3>
              <p className="mt-2 text-sm text-slate-500">
                Cuando el BFF entregue proyectos, apareceran en este listado.
              </p>
            </div>
          ) : (
            <div className="grid gap-4 p-5 lg:grid-cols-2">
              {projects.map((project) => (
                <article
                  key={project.id}
                  className="rounded-xl border border-slate-200 p-5"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <span className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                        {project.codigo}
                      </span>
                      <h3 className="mt-1 text-lg font-bold text-slate-950">
                        {project.nombre}
                      </h3>
                    </div>

                    <span
                      className={[
                        "shrink-0 rounded-full px-3 py-1 text-xs font-bold ring-1",
                        statusStyles[project.estado] ??
                          "bg-slate-50 text-slate-700 ring-slate-100",
                      ].join(" ")}
                    >
                      {statusLabels[project.estado] ?? project.estado}
                    </span>
                  </div>

                  <p className="mt-3 line-clamp-2 text-sm text-slate-500">
                    {project.descripcion || "Sin descripcion registrada."}
                  </p>

                  <div className="mt-5 grid grid-cols-2 gap-3 border-t border-slate-100 pt-4 text-sm">
                    <div>
                      <p className="text-slate-400">Inicio</p>
                      <p className="font-semibold text-slate-700">
                        {formatDate(project.fechaInicio)}
                      </p>
                    </div>
                    <div>
                      <p className="text-slate-400">Fin</p>
                      <p className="font-semibold text-slate-700">
                        {formatDate(project.fechaFin)}
                      </p>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>

      <aside className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <div>
          <h2 className="text-xl font-bold text-slate-950">Nuevo proyecto</h2>
          <p className="mt-1 text-sm text-slate-500">
            Valida campos obligatorios antes de enviar al backend.
          </p>
        </div>

        {successMessage && (
          <div className="mt-4 rounded-lg border border-emerald-100 bg-emerald-50 p-3 text-sm font-medium text-emerald-700">
            {successMessage}
          </div>
        )}

        <form className="mt-5 space-y-4" onSubmit={handleSubmit} noValidate>
          <div>
            <label
              htmlFor="codigo"
              className="text-sm font-semibold text-slate-700"
            >
              Codigo
            </label>
            <input
              id="codigo"
              value={form.codigo}
              onChange={(event) => updateField("codigo", event.target.value)}
              placeholder="PRY-001"
              className="mt-1 h-10 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
            />
            {formErrors.codigo && (
              <p className="mt-1 text-xs font-medium text-red-600">
                {formErrors.codigo}
              </p>
            )}
          </div>

          <div>
            <label
              htmlFor="nombre"
              className="text-sm font-semibold text-slate-700"
            >
              Nombre
            </label>
            <input
              id="nombre"
              value={form.nombre}
              onChange={(event) => updateField("nombre", event.target.value)}
              placeholder="Portal de clientes"
              className="mt-1 h-10 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
            />
            {formErrors.nombre && (
              <p className="mt-1 text-xs font-medium text-red-600">
                {formErrors.nombre}
              </p>
            )}
          </div>

          <div>
            <label
              htmlFor="descripcion"
              className="text-sm font-semibold text-slate-700"
            >
              Descripcion
            </label>
            <textarea
              id="descripcion"
              value={form.descripcion}
              onChange={(event) =>
                updateField("descripcion", event.target.value)
              }
              rows={4}
              placeholder="Describe el objetivo del proyecto"
              className="mt-1 w-full resize-none rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-1">
            <div>
              <label
                htmlFor="fechaInicio"
                className="text-sm font-semibold text-slate-700"
              >
                Fecha inicio
              </label>
              <input
                id="fechaInicio"
                inputMode="numeric"
                value={form.fechaInicio}
                onChange={(event) =>
                  updateField("fechaInicio", event.target.value)
                }
                placeholder="2026-07-01"
                className="mt-1 h-10 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
              />
              {formErrors.fechaInicio && (
                <p className="mt-1 text-xs font-medium text-red-600">
                  {formErrors.fechaInicio}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="fechaFin"
                className="text-sm font-semibold text-slate-700"
              >
                Fecha fin
              </label>
              <input
                id="fechaFin"
                inputMode="numeric"
                value={form.fechaFin}
                onChange={(event) =>
                  updateField("fechaFin", event.target.value)
                }
                placeholder="2026-12-31"
                className="mt-1 h-10 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
              />
              {formErrors.fechaFin && (
                <p className="mt-1 text-xs font-medium text-red-600">
                  {formErrors.fechaFin}
                </p>
              )}
            </div>
          </div>

          <div>
            <label
              htmlFor="estado"
              className="text-sm font-semibold text-slate-700"
            >
              Estado
            </label>
            <select
              id="estado"
              value={form.estado}
              onChange={(event) =>
                updateField("estado", event.target.value as ProjectStatus)
              }
              className="mt-1 h-10 w-full rounded-lg border border-slate-200 bg-white px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
            >
              {projectStatuses.map((status) => (
                <option key={status} value={status}>
                  {statusLabels[status]}
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full rounded-lg bg-cyan-500 px-4 py-3 text-sm font-bold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSubmitting ? "Creando..." : "Crear proyecto"}
          </button>
        </form>
      </aside>
    </div>
  );
}
