export type Role = 'ADMIN' | 'USER';

export type ApplicationStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED';

export interface ApiResponse<T> {
  requestId: string;
  data: T;
}

export interface ApiErrorResponse {
  requestId: string;
  code: string;
  message: string;
  details: string[];
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  loginId: string;
  displayName: string;
  role: Role;
}

export interface CurrentUserResponse {
  userId: number;
  loginId: string;
  displayName: string;
  role: Role;
}

export interface ApplicationSummary {
  id: number;
  applicantUserId: number;
  newAddress: string;
  status: ApplicationStatus;
  submittedAt: string | null;
  updatedAt: string;
}

export interface ApplicationDetail {
  id: number;
  applicantUserId: number;
  currentAddress: string;
  newAddress: string;
  reason: string;
  status: ApplicationStatus;
  submittedAt: string | null;
  decidedAt: string | null;
  decidedByUserId: number | null;
  rejectionReason: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ApplicationHistory {
  id: number;
  fromStatus: ApplicationStatus;
  toStatus: ApplicationStatus;
  actedByUserId: number;
  comment: string | null;
  actedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AuditLog {
  id: number;
  requestId: string;
  actorUserId: number | null;
  actionType: string;
  targetType: string;
  targetId: string | null;
  result: 'SUCCESS' | 'FAILURE';
  detailsJson: string;
  createdAt: string;
}
