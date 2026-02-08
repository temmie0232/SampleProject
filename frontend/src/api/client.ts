import axios, { AxiosError } from 'axios';
import type { ApiErrorResponse } from './types';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL,
  timeout: 10000
});

apiClient.interceptors.request.use((config) => {
  // 認証ヘッダとX-Request-Idを共通化し、各画面からインフラ実装を隠蔽する。
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  const requestId = crypto.randomUUID();
  config.headers['X-Request-Id'] = requestId;
  return config;
});

export function extractApiError(error: unknown): string {
  if (error instanceof AxiosError) {
    const body = error.response?.data as ApiErrorResponse | undefined;
    if (body?.details?.length) {
      return `${body.message} (${body.details.join(', ')})`;
    }
    if (body?.message) {
      return body.message;
    }
    return error.message;
  }
  return '不明なエラーが発生しました';
}
