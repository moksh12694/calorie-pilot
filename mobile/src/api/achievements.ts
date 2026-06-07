import { request } from './client';

export interface AchievementResponse {
  id: number;
  code: string;
  title: string;
  description: string;
  icon: string | null;
  threshold: number | null;
  earned: boolean;
  earnedAt: string | null;
}

export interface StreakResponse {
  currentDays: number;
  longestDays: number;
  lastGoalDate: string | null;
}

export const achievementsApi = {
  list: () => request<AchievementResponse[]>({ url: '/achievements', method: 'GET' }),
  streak: () => request<StreakResponse>({ url: '/achievements/streak', method: 'GET' }),
};
