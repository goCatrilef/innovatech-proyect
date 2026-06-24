export type ProjectStatus =
  | "PLANIFICADO"
  | "EN_PROGRESO"
  | "FINALIZADO"
  | "CANCELADO";

export type Project = {
  id: number;
  codigo: string;
  nombre: string;
  descripcion?: string | null;
  fechaInicio: string;
  fechaFin?: string | null;
  estado: ProjectStatus;
  fechaCreacion?: string;
};

export type CreateProjectRequest = {
  codigo: string;
  nombre: string;
  descripcion?: string;
  fechaInicio: string;
  fechaFin?: string;
  estado: ProjectStatus;
};

export type ProjectSummary = {
  proyecto: {
    id: number;
    codigo: string;
    nombre: string;
    descripcion?: string | null;
    fechaInicio?: string;
    fechaFin?: string | null;
    estado: ProjectStatus | string;
  };
  miembros: unknown[];
  tareas: unknown[];
  totalTareas: number;
  tareasPendientes: number;
  tareasEnProgreso: number;
  tareasFinalizadas: number;
};
