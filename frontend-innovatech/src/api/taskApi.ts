import { bffApi } from "./bffApi";
import type { CreateTaskRequest, Task } from "../types/task";

const TASKS_PATH = "/api/bff/tareas";

export async function getTasksByProject(projectId: number) {
  const response = await bffApi.get<Task[]>(
    `${TASKS_PATH}/proyecto/${projectId}`
  );
  return response.data;
}

export async function createTask(task: CreateTaskRequest) {
  const response = await bffApi.post<Task>(TASKS_PATH, task);
  return response.data;
}
