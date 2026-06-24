export type TeamMember = {
  id: number;
  identificador: string;
  nombre: string;
  rol: string;
  email?: string | null;
  activo: boolean;
  fechaCreacion?: string;
};

export type CreateTeamMemberRequest = {
  identificador: string;
  nombre: string;
  rol: string;
  email?: string;
  activo: boolean;
};

export type ProjectMember = {
  id: number;
  miembroId: number;
  nombreMiembro: string;
  rolMiembro: string;
  proyectoId: number;
  fechaAsignacion?: string;
};

export type AssignMemberToProjectRequest = {
  miembroId: number;
  proyectoId: number;
};
