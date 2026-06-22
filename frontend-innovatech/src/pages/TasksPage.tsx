import { useEffect, useMemo, useState, type FormEvent } from "react";
import { getApiErrorMessage, getApiErrorStatus } from "../api/apiError";
import { getProjects } from "../api/projectApi";
import { createTask, getTasksByProject } from "../api/taskApi";
import type { Project } from "../types/project";
import type { CreateTaskRequest, Task, TaskStatus } from "../types/task";

type TaskFormState = {
  descripcion: string;
  responsableId: string;
  estado: TaskStatus;
};

type TaskFormErrors = Partial<Record<keyof TaskFormState, string>>;

type TaskColumn = {
  status: TaskStatus;
  title: string;
  description: string;
  accentClassName: string;
};

const taskColumns: TaskColumn[] = [
  {
    status: "PENDING",
    title: "Pendientes",
    description: "Tareas planificadas para iniciar.",
    accentClassName: "border-t-slate-400",
  },
  {
    status: "IN_PROGRESS",
    title: "En progreso",
    description: "Trabajo activo del equipo.",
    accentClassName: "border-t-cyan-400",
  },
  {
    status: "DONE",
    title: "Finalizadas",
    description: "Tareas completadas.",
    accentClassName: "border-t-emerald-400",
  },
];

const statusLabels: Record<TaskStatus, string> = {
  PENDING: "Pendiente",
  IN_PROGRESS: "En progreso",
  DONE: "Finalizada",
};

const initialFormState: TaskFormState = {
  descripcion: "",
  responsableId: "",
  estado: "PENDING",
};

function validateTaskForm(form: TaskFormState, selectedProjectId?: number) {
  const errors: TaskFormErrors = {};

  if (!selectedProjectId) {
    errors.descripcion = "Selecciona un proyecto antes de crear tareas.";
  }

  if (!form.descripcion.trim()) {
    errors.descripcion = "La descripcion es obligatoria.";
  }

  if (!form.responsableId.trim()) {
    errors.responsableId = "El responsable es obligatorio.";
  } else if (Number(form.responsableId) <= 0) {
    errors.responsableId = "El responsable debe ser un id mayor a cero.";
  }

  return errors;
}

function toCreateTaskRequest(
  form: TaskFormState,
  selectedProjectId: number
): CreateTaskRequest {
  return {
    descripcion: form.descripcion.trim(),
    proyectoId: selectedProjectId,
    responsableId: Number(form.responsableId),
    estado: form.estado,
  };
}

export function TasksPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<number>();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [form, setForm] = useState<TaskFormState>(initialFormState);
  const [formErrors, setFormErrors] = useState<TaskFormErrors>({});
  const [isLoadingProjects, setIsLoadingProjects] = useState(true);
  const [isLoadingTasks, setIsLoadingTasks] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const selectedProject = useMemo(
    () => projects.find((project) => project.id === selectedProjectId),
    [projects, selectedProjectId]
  );

  const tasksByStatus = useMemo(
    () =>
      taskColumns.reduce<Record<TaskStatus, Task[]>>(
        (accumulator, column) => ({
          ...accumulator,
          [column.status]: tasks.filter((task) => task.estado === column.status),
        }),
        {
          PENDING: [],
          IN_PROGRESS: [],
          DONE: [],
        }
      ),
    [tasks]
  );

  useEffect(() => {
    const loadProjects = async () => {
      try {
        setError("");
        setIsLoadingProjects(true);

        const data = await getProjects();
        setProjects(data);

        if (data.length > 0) {
          setSelectedProjectId(data[0].id);
        }
      } catch (cause) {
        setError(
          getApiErrorMessage(
            cause,
            "No fue posible cargar los proyectos para consultar tareas."
          )
        );
      } finally {
        setIsLoadingProjects(false);
      }
    };

    void loadProjects();
  }, []);

  useEffect(() => {
    if (!selectedProjectId) {
      setTasks([]);
      return;
    }

    const loadTasks = async () => {
      try {
        setError("");
        setIsLoadingTasks(true);

        const data = await getTasksByProject(selectedProjectId);
        setTasks(data);
      } catch (cause) {
        if (getApiErrorStatus(cause) === 404) {
          setError(
            "El BFF aun no expone GET /api/bff/tareas/proyecto/{proyectoId}."
          );
        } else {
          setError(
            getApiErrorMessage(
              cause,
              "No fue posible cargar las tareas desde el BFF."
            )
          );
        }
      } finally {
        setIsLoadingTasks(false);
      }
    };

    void loadTasks();
  }, [selectedProjectId]);

  const updateField = (field: keyof TaskFormState, value: string) => {
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

    const errors = validateTaskForm(form, selectedProjectId);
    setFormErrors(errors);
    setSuccessMessage("");

    if (Object.keys(errors).length > 0 || !selectedProjectId) {
      return;
    }

    try {
      setError("");
      setIsSubmitting(true);

      const createdTask = await createTask(
        toCreateTaskRequest(form, selectedProjectId)
      );

      setTasks((currentTasks) => [createdTask, ...currentTasks]);
      setForm(initialFormState);
      setSuccessMessage("Tarea creada correctamente.");
    } catch (cause) {
      if (getApiErrorStatus(cause) === 404) {
        setError(
          "El BFF aun no expone POST /api/bff/tareas. No se llamo directo al microservicio para respetar la arquitectura."
        );
      } else {
        setError(
          getApiErrorMessage(cause, "No fue posible crear la tarea desde el BFF.")
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 className="text-xl font-bold text-slate-950">
              Tablero por proyecto
            </h2>
            <p className="mt-1 text-sm text-slate-500">
              Las tareas se cargan desde el BFF y se agrupan por estado.
            </p>
          </div>

          <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
            <label
              htmlFor="project-selector"
              className="text-sm font-semibold text-slate-700"
            >
              Proyecto
            </label>
            <select
              id="project-selector"
              value={selectedProjectId ?? ""}
              onChange={(event) =>
                setSelectedProjectId(Number(event.target.value))
              }
              disabled={isLoadingProjects || projects.length === 0}
              className="h-10 min-w-72 rounded-lg border border-slate-200 bg-white px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {projects.length === 0 ? (
                <option value="">Sin proyectos disponibles</option>
              ) : (
                projects.map((project) => (
                  <option key={project.id} value={project.id}>
                    {project.codigo} - {project.nombre}
                  </option>
                ))
              )}
            </select>
          </div>
        </div>

        {selectedProject && (
          <div className="mt-4 rounded-lg bg-slate-50 p-4 text-sm text-slate-600">
            Mostrando tareas de{" "}
            <span className="font-semibold text-slate-950">
              {selectedProject.nombre}
            </span>
            .
          </div>
        )}

        {error && (
          <div className="mt-4 rounded-lg border border-red-100 bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        )}
      </section>

      <section className="grid gap-4 sm:grid-cols-3">
        {taskColumns.map((column) => (
          <article
            key={column.status}
            className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm"
          >
            <p className="text-sm font-medium text-slate-500">
              {column.title}
            </p>
            <p className="mt-2 text-3xl font-bold text-slate-950">
              {tasksByStatus[column.status].length}
            </p>
          </article>
        ))}
      </section>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="grid gap-4 lg:grid-cols-3">
          {taskColumns.map((column) => (
            <article
              key={column.status}
              className={[
                "min-h-96 rounded-xl border border-t-4 border-slate-200 bg-white p-4 shadow-sm",
                column.accentClassName,
              ].join(" ")}
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h3 className="font-bold text-slate-950">{column.title}</h3>
                  <p className="mt-1 text-xs text-slate-500">
                    {column.description}
                  </p>
                </div>
                <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-bold text-slate-600">
                  {tasksByStatus[column.status].length}
                </span>
              </div>

              <div className="mt-4 space-y-3">
                {isLoadingTasks ? (
                  <div className="h-28 animate-pulse rounded-lg bg-slate-100" />
                ) : tasksByStatus[column.status].length === 0 ? (
                  <div className="rounded-lg border border-dashed border-slate-300 bg-slate-50 p-5 text-center text-sm text-slate-500">
                    Sin tareas en este estado.
                  </div>
                ) : (
                  tasksByStatus[column.status].map((task) => (
                    <article
                      key={task.id}
                      className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <p className="font-semibold text-slate-950">
                          {task.descripcion}
                        </p>
                        <span className="rounded-full bg-slate-100 px-2 py-1 text-[11px] font-bold text-slate-600">
                          #{task.id}
                        </span>
                      </div>
                      <div className="mt-4 flex items-center justify-between border-t border-slate-100 pt-3 text-xs text-slate-500">
                        <span>Responsable: {task.responsableId}</span>
                        <span>{statusLabels[task.estado]}</span>
                      </div>
                    </article>
                  ))
                )}
              </div>
            </article>
          ))}
        </section>

        <aside className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-xl font-bold text-slate-950">Nueva tarea</h2>
          <p className="mt-1 text-sm text-slate-500">
            Crea una tarea asociada al proyecto seleccionado.
          </p>

          {successMessage && (
            <div className="mt-4 rounded-lg border border-emerald-100 bg-emerald-50 p-3 text-sm font-medium text-emerald-700">
              {successMessage}
            </div>
          )}

          <form className="mt-5 space-y-4" onSubmit={handleSubmit} noValidate>
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
                placeholder="Implementar validacion de formulario"
                className="mt-1 w-full resize-none rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
              />
              {formErrors.descripcion && (
                <p className="mt-1 text-xs font-medium text-red-600">
                  {formErrors.descripcion}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="responsableId"
                className="text-sm font-semibold text-slate-700"
              >
                Responsable ID
              </label>
              <input
                id="responsableId"
                inputMode="numeric"
                value={form.responsableId}
                onChange={(event) =>
                  updateField("responsableId", event.target.value)
                }
                placeholder="1"
                className="mt-1 h-10 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
              />
              {formErrors.responsableId && (
                <p className="mt-1 text-xs font-medium text-red-600">
                  {formErrors.responsableId}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="estado"
                className="text-sm font-semibold text-slate-700"
              >
                Estado inicial
              </label>
              <select
                id="estado"
                value={form.estado}
                onChange={(event) =>
                  updateField("estado", event.target.value as TaskStatus)
                }
                className="mt-1 h-10 w-full rounded-lg border border-slate-200 bg-white px-3 text-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100"
              >
                {taskColumns.map((column) => (
                  <option key={column.status} value={column.status}>
                    {statusLabels[column.status]}
                  </option>
                ))}
              </select>
            </div>

            <button
              type="submit"
              disabled={isSubmitting || !selectedProjectId}
              className="w-full rounded-lg bg-cyan-500 px-4 py-3 text-sm font-bold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isSubmitting ? "Creando..." : "Crear tarea"}
            </button>
          </form>
        </aside>
      </div>
    </div>
  );
}
