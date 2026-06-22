import { bffApi } from "./bffApi";
import type { CreateProjectRequest, Project } from "../types/project";

const PROJECTS_PATH = "/api/bff/proyectos";

export async function getProjects() {
  const response = await bffApi.get<Project[]>(PROJECTS_PATH);
  return response.data;
}

export async function createProject(project: CreateProjectRequest) {
  const response = await bffApi.post<Project>(PROJECTS_PATH, project);
  return response.data;
}
