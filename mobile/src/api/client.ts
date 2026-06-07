import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import Constants from 'expo-constants';

import { store } from '@/store';
import { clearSession } from '@/store/slices/authSlice';

const baseURL =
  process.env.EXPO_PUBLIC_API_BASE_URL ||
  (Constants.expoConfig?.extra as { apiBaseUrl?: string } | undefined)?.apiBaseUrl ||
  'http://localhost:8080/api';

export const apiClient = axios.create({
  baseURL,
  timeout: 15_000,
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use((config) => {
  const token = store.getState().auth.accessToken;
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Module 3 will replace this with a refresh-token flow.
      store.dispatch(clearSession());
    }
    return Promise.reject(error);
  }
);

export interface ApiEnvelope<T> {
  success: boolean;
  data?: T;
  error?: { code: string; message: string; fieldErrors?: Record<string, string[]> };
  timestamp: string;
}

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const { data } = await apiClient.request<ApiEnvelope<T>>(config);
  if (!data.success || data.data === undefined) {
    throw new Error(data.error?.message ?? 'Unknown API error');
  }
  return data.data;
}
