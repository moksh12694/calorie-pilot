import { request } from './client';

export interface StepLogResponse {
  date: string;
  steps: number;
  distanceM: number | null;
  calories: number | null;
  goal: number;
  progressPct: number;
  goalMet: boolean;
}

export interface StepGoalView {
  dailyStepGoal: number;
}

export const stepsApi = {
  sync: (body: { date: string; steps: number; distanceM?: number; calories?: number }) =>
    request<StepLogResponse>({ url: '/steps/sync', method: 'POST', data: body }),

  today: () => request<StepLogResponse>({ url: '/steps/today', method: 'GET' }),

  history: (from: string, to: string) =>
    request<StepLogResponse[]>({ url: '/steps/history', method: 'GET', params: { from, to } }),

  getGoal: () => request<StepGoalView>({ url: '/steps/goal', method: 'GET' }),

  updateGoal: (dailyStepGoal: number) =>
    request<StepGoalView>({ url: '/steps/goal', method: 'PUT', data: { dailyStepGoal } }),
};
