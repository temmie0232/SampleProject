import { apiClient } from './client';
import type { ApiResponse, CurrentUserResponse, LoginResponse } from './types';

export interface LoginRequest {
  loginId: string;
  password: string;
}

export async function loginApi(request: LoginRequest): Promise<LoginResponse> {
  const response = await apiClient.post<ApiResponse<LoginResponse>>('/api/v1/auth/login', request);
  return response.data.data;
}

export async function meApi(): Promise<CurrentUserResponse> {
  const response = await apiClient.get<ApiResponse<CurrentUserResponse>>('/api/v1/auth/me');
  return response.data.data;
}
