import { request } from './client';

export interface WeightEntry {
  id: number;
  date: string;
  weightKg: number;
  note: string | null;
}

export interface WeightHistoryResponse {
  latestKg: number | null;
  earliestKg: number | null;
  totalDeltaKg: number | null;
  targetKg: number | null;
  entries: WeightEntry[];
}

export const weightApi = {
  log: (body: { date: string; weightKg: number; note?: string }) =>
    request<WeightEntry>({ url: '/weight', method: 'POST', data: body }),

  delete: (id: number) => request<void>({ url: `/weight/${id}`, method: 'DELETE' }),

  history: (from: string, to: string) =>
    request<WeightHistoryResponse>({ url: '/weight/history', method: 'GET', params: { from, to } }),
};
