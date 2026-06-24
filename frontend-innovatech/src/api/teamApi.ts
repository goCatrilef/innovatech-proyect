import { bffApi } from "./bffApi";
import type {
  AssignMemberToProjectRequest,
  CreateTeamMemberRequest,
  ProjectMember,
  TeamMember,
} from "../types/team";

const TEAM_PATH = "/api/bff/equipos";

export async function getTeamMembers() {
  const response = await bffApi.get<TeamMember[]>(`${TEAM_PATH}/miembros`);
  return response.data;
}

export async function createTeamMember(member: CreateTeamMemberRequest) {
  const response = await bffApi.post<TeamMember>(
    `${TEAM_PATH}/miembros`,
    member
  );
  return response.data;
}

export async function assignMemberToProject(
  assignment: AssignMemberToProjectRequest
) {
  const response = await bffApi.post<ProjectMember>(
    `${TEAM_PATH}/asignaciones`,
    assignment
  );
  return response.data;
}

export async function getProjectMembers(projectId: number) {
  const response = await bffApi.get<ProjectMember[]>(
    `${TEAM_PATH}/proyectos/${projectId}/miembros`
  );
  return response.data;
}
