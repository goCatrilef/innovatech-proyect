import { useEffect, useMemo, useState, type FormEvent } from "react";
import { getApiErrorMessage, getApiErrorStatus } from "../api/apiError";
import { getProjects } from "../api/projectApi";
import {
  assignMemberToProject,
  createTeamMember,
  getProjectMembers,
  getTeamMembers,
} from "../api/teamApi";
import type { Project } from "../types/project";
import type { ProjectMember, TeamMember } from "../types/team";

type MemberFormState = {
  identificador: string;
  nombre: string;
  rol: string;
  email: string;
};

type MemberFormErrors = Partial<Record<keyof MemberFormState, string>>;

const initialMemberForm: MemberFormState = {
  identificador: "",
  nombre: "",
  rol: "",
  email: "",
};

function validateMemberForm(form: MemberFormState) {
  const errors: MemberFormErrors = {};

  if (!form.identificador.trim()) {
    errors.identificador = "El identificador es obligatorio.";
  }

  if (!form.nombre.trim()) {
    errors.nombre = "El nombre es obligatorio.";
  }

  if (!form.rol.trim()) {
    errors.rol = "El rol es obligatorio.";
  }

  if (form.email && !form.email.includes("@")) {
    errors.email = "Ingresa un email valido.";
  }

  return errors;
}

export function TeamPage() {
  const [members, setMembers] = useState<TeamMember[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [projectMembers, setProjectMembers] = useState<ProjectMember[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<number>();
  const [selectedMemberId, setSelectedMemberId] = useState<number>();
  const [memberForm, setMemberForm] =
    useState<MemberFormState>(initialMemberForm);
  const [memberFormErrors, setMemberFormErrors] = useState<MemberFormErrors>(
    {}
  );
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingProjectMembers, setIsLoadingProjectMembers] = useState(false);
  const [isCreatingMember, setIsCreatingMember] = useState(false);
  const [isAssigning, setIsAssigning] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const activeMembers = useMemo(
    () => members.filter((member) => member.activo),
    [members]
  );

  const selectedProject = useMemo(
    () => projects.find((project) => project.id === selectedProjectId),
    [projects, selectedProjectId]
  );

  const loadTeamData = async () => {
    try {
      setError("");
      setIsLoading(true);

      const [membersData, projectsData] = await Promise.all([
        getTeamMembers(),
        getProjects(),
      ]);

      setMembers(membersData);
      setProjects(projectsData);

      if (projectsData.length > 0) {
        setSelectedProjectId(projectsData[0].id);
      }

      if (membersData.length > 0) {
        setSelectedMemberId(membersData[0].id);
      }
    } catch (cause) {
      setError(
        getApiErrorMessage(
          cause,
          "No fue posible cargar miembros y proyectos desde el BFF."
        )
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void loadTeamData();
  }, []);

  useEffect(() => {
    if (!selectedProjectId) {
      setProjectMembers([]);
      return;
    }

    const loadProjectMembers = async () => {
      try {
        setError("");
        setIsLoadingProjectMembers(true);
        const data = await getProjectMembers(selectedProjectId);
        setProjectMembers(data);
      } catch (cause) {
        if (getApiErrorStatus(cause) === 404) {
          setError(
            "El BFF aun no expone GET /api/bff/equipos/proyectos/{proyectoId}/miembros."
          );
        } else {
          setError(
            getApiErrorMessage(
              cause,
              "No fue posible cargar miembros asignados al proyecto."
            )
          );
        }
      } finally {
        setIsLoadingProjectMembers(false);
      }
    };

    void loadProjectMembers();
  }, [selectedProjectId]);

  const updateMemberField = (field: keyof MemberFormState, value: string) => {
    setMemberForm((currentForm) => ({
      ...currentForm,
      [field]: value,
    }));

    setMemberFormErrors((currentErrors) => ({
      ...currentErrors,
      [field]: undefined,
    }));
  };

  const handleCreateMember = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const errors = validateMemberForm(memberForm);
    setMemberFormErrors(errors);
    setSuccessMessage("");

    if (Object.keys(errors).length > 0) {
      return;
    }

    try {
      setError("");
      setIsCreatingMember(true);

      const createdMember = await createTeamMember({
        identificador: memberForm.identificador.trim(),
        nombre: memberForm.nombre.trim(),
        rol: memberForm.rol.trim(),
        email: memberForm.email.trim() || undefined,
        activo: true,
      });

      setMembers((currentMembers) => [createdMember, ...currentMembers]);
      setSelectedMemberId(createdMember.id);
      setMemberForm(initialMemberForm);
      setSuccessMessage("Miembro creado correctamente.");
    } catch (cause) {
      setError(
        getApiErrorMessage(cause, "No fue posible crear el miembro desde el BFF.")
      );
    } finally {
      setIsCreatingMember(false);
    }
  };

  const handleAssignMember = async () => {
    setSuccessMessage("");

    if (!selectedMemberId || !selectedProjectId) {
      setError("Selecciona un miembro y un proyecto para crear la asignacion.");
      return;
    }

    try {
      setError("");
      setIsAssigning(true);

      const assignment = await assignMemberToProject({
        miembroId: selectedMemberId,
        proyectoId: selectedProjectId,
      });

      setProjectMembers((currentProjectMembers) => [
        assignment,
        ...currentProjectMembers,
      ]);
      setSuccessMessage("Miembro asociado al proyecto correctamente.");
    } catch (cause) {
      setError(
        getApiErrorMessage(
          cause,
          "No fue posible asociar el miembro al proyecto."
        )
      );
    } finally {
      setIsAssigning(false);
    }
  };

  return (
    <div className="space-y-6">
      <section className="grid gap-4 sm:grid-cols-3">
        <article className="rounded-xl border border-neutral-200 bg-white p-5 shadow-card">
          <p className="text-sm font-medium text-neutral-500">Total miembros</p>
          <p className="mt-2 text-3xl font-bold text-neutral-900">
            {members.length}
          </p>
        </article>
        <article className="rounded-xl border border-neutral-200 bg-white p-5 shadow-card">
          <p className="text-sm font-medium text-neutral-500">
            Miembros activos
          </p>
          <p className="mt-2 text-3xl font-bold text-neutral-900">
            {activeMembers.length}
          </p>
        </article>
        <article className="rounded-xl border border-neutral-200 bg-white p-5 shadow-card">
          <p className="text-sm font-medium text-neutral-500">
            Asignados al proyecto
          </p>
          <p className="mt-2 text-3xl font-bold text-neutral-900">
            {projectMembers.length}
          </p>
        </article>
      </section>

      {error && (
        <div className="rounded-lg border border-danger-100 bg-danger-50 p-4 text-sm text-danger-600">
          {error}
        </div>
      )}

      {successMessage && (
        <div className="rounded-lg border border-success-100 bg-success-50 p-4 text-sm font-medium text-success-600">
          {successMessage}
        </div>
      )}

      <section className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_420px]">
        <div className="space-y-6">
          <section className="rounded-xl border border-neutral-200 bg-white shadow-card">
            <div className="border-b border-neutral-100 p-5">
              <h2 className="text-xl font-bold text-neutral-900">
                Miembros registrados
              </h2>
              <p className="mt-1 text-sm text-neutral-500">
                Listado obtenido desde el BFF.
              </p>
            </div>

            {isLoading ? (
              <div className="p-5">
                <div className="h-32 animate-pulse rounded-lg bg-neutral-100" />
              </div>
            ) : members.length === 0 ? (
              <div className="p-8 text-center text-sm text-neutral-500">
                No hay miembros registrados.
              </div>
            ) : (
              <div className="divide-y divide-neutral-100">
                {members.map((member) => (
                  <article
                    key={member.id}
                    className="flex flex-col gap-3 p-5 sm:flex-row sm:items-center sm:justify-between"
                  >
                    <div>
                      <p className="font-bold text-neutral-900">
                        {member.nombre}
                      </p>
                      <p className="text-sm text-neutral-500">
                        {member.identificador} - {member.rol}
                      </p>
                      {member.email && (
                        <p className="text-sm text-neutral-500">
                          {member.email}
                        </p>
                      )}
                    </div>
                    <span
                      className={[
                        "w-fit rounded-full px-3 py-1 text-xs font-bold",
                        member.activo
                          ? "bg-success-50 text-success-600"
                          : "bg-neutral-100 text-neutral-500",
                      ].join(" ")}
                    >
                      {member.activo ? "Activo" : "Inactivo"}
                    </span>
                  </article>
                ))}
              </div>
            )}
          </section>

          <section className="rounded-xl border border-neutral-200 bg-white p-5 shadow-card">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <h2 className="text-xl font-bold text-neutral-900">
                  Miembros por proyecto
                </h2>
                <p className="mt-1 text-sm text-neutral-500">
                  Consulta quienes estan asociados a cada proyecto.
                </p>
              </div>
              <select
                value={selectedProjectId ?? ""}
                onChange={(event) =>
                  setSelectedProjectId(Number(event.target.value))
                }
                disabled={projects.length === 0}
                className="h-10 min-w-72 rounded-lg border border-neutral-200 bg-white px-3 text-sm outline-none transition focus:border-accent-500 focus:ring-4 focus:ring-accent-100 disabled:cursor-not-allowed disabled:opacity-60"
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

            {selectedProject && (
              <p className="mt-4 rounded-lg bg-neutral-50 p-3 text-sm text-neutral-500">
                Proyecto seleccionado:{" "}
                <span className="font-semibold text-neutral-900">
                  {selectedProject.nombre}
                </span>
              </p>
            )}

            <div className="mt-4 grid gap-3 md:grid-cols-2">
              {isLoadingProjectMembers ? (
                <div className="h-24 animate-pulse rounded-lg bg-neutral-100" />
              ) : projectMembers.length === 0 ? (
                <div className="rounded-lg border border-dashed border-neutral-300 bg-neutral-50 p-5 text-center text-sm text-neutral-500 md:col-span-2">
                  Este proyecto aun no tiene miembros asignados.
                </div>
              ) : (
                projectMembers.map((assignment) => (
                  <article
                    key={assignment.id}
                    className="rounded-lg border border-neutral-200 p-4"
                  >
                    <p className="font-bold text-neutral-900">
                      {assignment.nombreMiembro}
                    </p>
                    <p className="mt-1 text-sm text-neutral-500">
                      {assignment.rolMiembro}
                    </p>
                    <p className="mt-3 text-xs font-semibold text-accent-600">
                      Miembro #{assignment.miembroId}
                    </p>
                  </article>
                ))
              )}
            </div>
          </section>
        </div>

        <aside className="space-y-6">
          <section className="rounded-xl border border-neutral-200 bg-white p-5 shadow-card">
            <h2 className="text-xl font-bold text-neutral-900">Nuevo miembro</h2>
            <p className="mt-1 text-sm text-neutral-500">
              Registra miembros del equipo con validacion previa.
            </p>

            <form className="mt-5 space-y-4" onSubmit={handleCreateMember}>
              <div>
                <label
                  htmlFor="identificador"
                  className="text-sm font-semibold text-neutral-700"
                >
                  Identificador
                </label>
                <input
                  id="identificador"
                  value={memberForm.identificador}
                  onChange={(event) =>
                    updateMemberField("identificador", event.target.value)
                  }
                  placeholder="USR-001"
                  className="mt-1 h-10 w-full rounded-lg border border-neutral-200 px-3 text-sm outline-none transition focus:border-accent-500 focus:ring-4 focus:ring-accent-100"
                />
                {memberFormErrors.identificador && (
                  <p className="mt-1 text-xs font-medium text-danger-600">
                    {memberFormErrors.identificador}
                  </p>
                )}
              </div>

              <div>
                <label
                  htmlFor="nombre"
                  className="text-sm font-semibold text-neutral-700"
                >
                  Nombre
                </label>
                <input
                  id="nombre"
                  value={memberForm.nombre}
                  onChange={(event) =>
                    updateMemberField("nombre", event.target.value)
                  }
                  placeholder="Gonzalo Perez"
                  className="mt-1 h-10 w-full rounded-lg border border-neutral-200 px-3 text-sm outline-none transition focus:border-accent-500 focus:ring-4 focus:ring-accent-100"
                />
                {memberFormErrors.nombre && (
                  <p className="mt-1 text-xs font-medium text-danger-600">
                    {memberFormErrors.nombre}
                  </p>
                )}
              </div>

              <div>
                <label
                  htmlFor="rol"
                  className="text-sm font-semibold text-neutral-700"
                >
                  Rol
                </label>
                <input
                  id="rol"
                  value={memberForm.rol}
                  onChange={(event) =>
                    updateMemberField("rol", event.target.value)
                  }
                  placeholder="Frontend Developer"
                  className="mt-1 h-10 w-full rounded-lg border border-neutral-200 px-3 text-sm outline-none transition focus:border-accent-500 focus:ring-4 focus:ring-accent-100"
                />
                {memberFormErrors.rol && (
                  <p className="mt-1 text-xs font-medium text-danger-600">
                    {memberFormErrors.rol}
                  </p>
                )}
              </div>

              <div>
                <label
                  htmlFor="email"
                  className="text-sm font-semibold text-neutral-700"
                >
                  Email
                </label>
                <input
                  id="email"
                  value={memberForm.email}
                  onChange={(event) =>
                    updateMemberField("email", event.target.value)
                  }
                  placeholder="persona@innovatech.cl"
                  className="mt-1 h-10 w-full rounded-lg border border-neutral-200 px-3 text-sm outline-none transition focus:border-accent-500 focus:ring-4 focus:ring-accent-100"
                />
                {memberFormErrors.email && (
                  <p className="mt-1 text-xs font-medium text-danger-600">
                    {memberFormErrors.email}
                  </p>
                )}
              </div>

              <button
                type="submit"
                disabled={isCreatingMember}
                className="w-full rounded-lg bg-primary-600 px-4 py-3 text-sm font-bold text-white transition hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isCreatingMember ? "Creando..." : "Crear miembro"}
              </button>
            </form>
          </section>

          <section className="rounded-xl border border-neutral-200 bg-white p-5 shadow-card">
            <h2 className="text-xl font-bold text-neutral-900">
              Asociar a proyecto
            </h2>
            <p className="mt-1 text-sm text-neutral-500">
              Selecciona un miembro activo y el proyecto actual.
            </p>

            <div className="mt-5 space-y-4">
              <div>
                <label
                  htmlFor="member-selector"
                  className="text-sm font-semibold text-neutral-700"
                >
                  Miembro
                </label>
                <select
                  id="member-selector"
                  value={selectedMemberId ?? ""}
                  onChange={(event) =>
                    setSelectedMemberId(Number(event.target.value))
                  }
                  disabled={activeMembers.length === 0}
                  className="mt-1 h-10 w-full rounded-lg border border-neutral-200 bg-white px-3 text-sm outline-none transition focus:border-accent-500 focus:ring-4 focus:ring-accent-100 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {activeMembers.length === 0 ? (
                    <option value="">Sin miembros activos</option>
                  ) : (
                    activeMembers.map((member) => (
                      <option key={member.id} value={member.id}>
                        {member.nombre} - {member.rol}
                      </option>
                    ))
                  )}
                </select>
              </div>

              <button
                type="button"
                onClick={handleAssignMember}
                disabled={isAssigning || !selectedMemberId || !selectedProjectId}
                className="w-full rounded-lg bg-neutral-900 px-4 py-3 text-sm font-bold text-white transition hover:bg-neutral-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isAssigning ? "Asociando..." : "Asociar miembro"}
              </button>
            </div>
          </section>
        </aside>
      </section>
    </div>
  );
}
