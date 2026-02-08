import { apiClient } from './client';
import type { ApiResponse, AuditLog, PageResponse } from './types';

export interface AuditSearchQuery {
  actorUserId?: number;
  actionType?: string;
  from?: string;
  to?: string;
  page?: number;
  size?: number;
}

export async function searchAuditLogs(query: AuditSearchQuery): Promise<PageResponse<AuditLog>> {
  const response = await apiClient.get<ApiResponse<PageResponse<AuditLog>>>('/api/v1/audit-logs', { params: query });
  return response.data.data;
}
