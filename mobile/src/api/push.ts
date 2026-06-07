import { request } from './client';

export interface PushDeviceResponse {
  id: number;
  expoPushToken: string;
  platform: string;
  lastSeenAt: string;
}

export const pushApi = {
  register: (expoPushToken: string, platform: 'ios' | 'android' | 'web') =>
    request<PushDeviceResponse>({ url: '/push/register', method: 'POST', data: { expoPushToken, platform } }),

  unregister: (token: string) =>
    request<void>({ url: '/push/register', method: 'DELETE', params: { token } }),
};
