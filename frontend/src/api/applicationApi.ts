import { apiClient } from './client';
import type {
  ApiResponse,
  ApplicationDetail,
  ApplicationHistory,
  ApplicationStatus,
  ApplicationSummary,
  PageResponse
} from './types';

export interface ApplicationSearchQuery {
  q?: string;
  status?: ApplicationStatus | '';
  page?: number;
  size?: number;
  sort?: string;
}

export interface SaveApplicationRequest {
  currentAddress: string;
  newAddress: string;
  reason: string;
}

export interface ActionRequest {
  comment?: string;
  rejectionReason?: string;
}

export async function searchApplications(query: ApplicationSearchQuery): Promise<PageResponse<ApplicationSummary>> {
  const response = await apiClient.get<ApiResponse<PageResponse<ApplicationSummary>>>('/api/v1/applications', {
    params: query
  });
  return response.data.data;
}

export async function createApplication(request: SaveApplicationRequest): Promise<ApplicationDetail> {
  const response = await apiClient.post<ApiResponse<ApplicationDetail>>('/api/v1/applications', request);
  return response.data.data;
}

export async function getApplication(id: number): Promise<ApplicationDetail> {
  const response = await apiClient.get<ApiResponse<ApplicationDetail>>(`/api/v1/applications/${id}`);
  return response.data.data;
}

export async function updateApplication(id: number, request: SaveApplicationRequest): Promise<ApplicationDetail> {
  const response = await apiClient.put<ApiResponse<ApplicationDetail>>(`/api/v1/applications/${id}`, request);
  return response.data.data;
}

export async function submitApplication(id: number, request: ActionRequest): Promise<ApplicationDetail> {
  const response = await apiClient.post<ApiResponse<ApplicationDetail>>(`/api/v1/applications/${id}/submit`, request);
  return response.data.data;
}

export async function approveApplication(id: number, request: ActionRequest): Promise<ApplicationDetail> {
  const response = await apiClient.post<ApiResponse<ApplicationDetail>>(`/api/v1/applications/${id}/approve`, request);
  return response.data.data;
}

export async function rejectApplication(id: number, request: ActionRequest): Promise<ApplicationDetail> {
  const response = await apiClient.post<ApiResponse<ApplicationDetail>>(`/api/v1/applications/${id}/reject`, request);
  return response.data.data;
}

export async function getApplicationHistory(id: number): Promise<ApplicationHistory[]> {
  const response = await apiClient.get<ApiResponse<ApplicationHistory[]>>(`/api/v1/applications/${id}/history`);
  return response.data.data;
}
