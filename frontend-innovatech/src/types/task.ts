export type TaskStatus = "PENDING" | "IN_PROGRESS" | "DONE";

export type Task = {
  id: number;
  descripcion: string;
  proyectoId: number;
  responsableId: number;
  estado: TaskStatus;
  fechaCreacion?: string;
  fechaActualizacion?: string;
};

export type CreateTaskRequest = {
  descripcion: string;
  proyectoId: number;
  responsableId: number;
  estado: TaskStatus;
};
