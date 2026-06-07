import { request } from './client';

export interface WaterEntry {
  id: number;
  amountMl: number;
  loggedAt: string;
}

export interface WaterDayResponse {
  date: string;
  totalMl: number;
  goalMl: number;
  progressPct: number;
  entries: WaterEntry[];
}

export const waterApi = {
  log: (date: string, amountMl: number) =>
    request<WaterEntry>({ url: '/water', method: 'POST', data: { date, amountMl } }),

  delete: (id: number) => request<void>({ url: `/water/${id}`, method: 'DELETE' }),

  day: (date: string) =>
    request<WaterDayResponse>({ url: '/water', method: 'GET', params: { date } }),
};
