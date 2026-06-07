import { request } from './client';

export interface UserResponse {
  id: string;
  email: string;
  name: string;
  dateOfBirth?: string | null;
  gender?: string | null;
  heightCm?: number | null;
  timezone: string;
}

export interface AuthResponse {
  user: UserResponse;
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
}

export const authApi = {
  signup: (body: { email: string; password: string; name: string }) =>
    request<AuthResponse>({ url: '/auth/signup', method: 'POST', data: body }),

  login: (body: { email: string; password: string }) =>
    request<AuthResponse>({ url: '/auth/login', method: 'POST', data: body }),

  refresh: (refreshToken: string) =>
    request<AuthResponse>({ url: '/auth/refresh', method: 'POST', data: { refreshToken } }),

  me: () => request<UserResponse>({ url: '/users/me', method: 'GET' }),
};
