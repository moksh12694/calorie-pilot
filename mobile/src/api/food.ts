import { request } from './client';

export type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';

export interface FoodResponse {
  id: number;
  name: string;
  brand: string | null;
  servingSizeG: number;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
  fiberG: number;
}

export interface MealEntryResponse {
  id: number;
  date: string;
  meal: MealType;
  foodId: number;
  foodName: string;
  servings: number;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
}

export interface DailySummaryResponse {
  date: string;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
  calorieGoal: number;
  proteinGoalG: number;
  carbsGoalG: number;
  fatGoalG: number;
  entries: MealEntryResponse[];
}

export const foodApi = {
  search: (q: string) => request<FoodResponse[]>({ url: '/foods', method: 'GET', params: { q } }),

  addEntry: (body: { date: string; meal: MealType; foodId: number; servings: number }) =>
    request<MealEntryResponse>({ url: '/meals', method: 'POST', data: body }),

  deleteEntry: (id: number) => request<void>({ url: `/meals/${id}`, method: 'DELETE' }),

  daily: (date: string) =>
    request<DailySummaryResponse>({ url: '/meals/daily', method: 'GET', params: { date } }),
};
