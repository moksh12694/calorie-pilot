import React, { useMemo, useState } from 'react';
import {
  View, Text, TextInput, FlatList, TouchableOpacity, StyleSheet, ActivityIndicator, Alert,
} from 'react-native';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { foodApi, FoodResponse, MealEntryResponse, MealType } from '@/api/food';
import { colors } from '@/theme/colors';

const MEALS: MealType[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

function todayISO() {
  return new Date().toISOString().slice(0, 10);
}

export function FoodLogScreen() {
  const qc = useQueryClient();
  const date = todayISO();
  const [meal, setMeal] = useState<MealType>('BREAKFAST');
  const [query, setQuery] = useState('');

  const daily = useQuery({
    queryKey: ['meals', 'daily', date],
    queryFn: () => foodApi.daily(date),
  });

  const search = useQuery({
    queryKey: ['foods', 'search', query],
    queryFn: () => foodApi.search(query),
    enabled: query.trim().length >= 2,
  });

  const addEntry = useMutation({
    mutationFn: (food: FoodResponse) =>
      foodApi.addEntry({ date, meal, foodId: food.id, servings: 1 }),
    onSuccess: () => {
      setQuery('');
      qc.invalidateQueries({ queryKey: ['meals', 'daily', date] });
    },
    onError: (e: Error) => Alert.alert('Could not add', e.message),
  });

  const deleteEntry = useMutation({
    mutationFn: (id: number) => foodApi.deleteEntry(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['meals', 'daily', date] }),
  });

  const grouped = useMemo(() => {
    const buckets: Record<MealType, MealEntryResponse[]> = { BREAKFAST: [], LUNCH: [], DINNER: [], SNACK: [] };
    (daily.data?.entries ?? []).forEach((e) => buckets[e.meal].push(e));
    return buckets;
  }, [daily.data]);

  return (
    <View style={styles.screen}>
      {/* totals */}
      {daily.data && (
        <View style={styles.totalsCard}>
          <Text style={styles.totalCal}>
            {Math.round(daily.data.calories)} / {daily.data.calorieGoal} kcal
          </Text>
          <Text style={styles.macros}>
            P {Math.round(daily.data.proteinG)}g · C {Math.round(daily.data.carbsG)}g · F {Math.round(daily.data.fatG)}g
          </Text>
        </View>
      )}

      {/* meal picker */}
      <View style={styles.tabs}>
        {MEALS.map((m) => (
          <TouchableOpacity key={m} style={[styles.tab, meal === m && styles.tabActive]} onPress={() => setMeal(m)}>
            <Text style={[styles.tabText, meal === m && styles.tabTextActive]}>{m[0] + m.slice(1).toLowerCase()}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* search */}
      <TextInput
        style={styles.input}
        placeholder="Search foods…"
        placeholderTextColor={colors.textMuted}
        value={query}
        onChangeText={setQuery}
        autoCapitalize="none"
      />

      {/* search results OR meal list */}
      {query.trim().length >= 2 ? (
        <FlatList
          data={search.data ?? []}
          keyExtractor={(f) => String(f.id)}
          ListEmptyComponent={
            search.isLoading ? <ActivityIndicator color={colors.primary} style={{ marginTop: 16 }} />
              : <Text style={styles.muted}>No matches</Text>
          }
          renderItem={({ item }) => (
            <TouchableOpacity style={styles.row} onPress={() => addEntry.mutate(item)}>
              <View style={{ flex: 1 }}>
                <Text style={styles.foodName}>{item.name}</Text>
                <Text style={styles.foodMeta}>{Math.round(item.calories)} kcal · per {item.servingSizeG}g</Text>
              </View>
              <Text style={styles.add}>+</Text>
            </TouchableOpacity>
          )}
        />
      ) : (
        <FlatList
          data={MEALS}
          keyExtractor={(m) => m}
          renderItem={({ item: m }) => (
            <View style={styles.section}>
              <Text style={styles.sectionTitle}>{m}</Text>
              {grouped[m].length === 0 && <Text style={styles.muted}>Nothing logged</Text>}
              {grouped[m].map((e) => (
                <TouchableOpacity key={e.id} style={styles.row} onLongPress={() => deleteEntry.mutate(e.id)}>
                  <View style={{ flex: 1 }}>
                    <Text style={styles.foodName}>{e.foodName} × {e.servings}</Text>
                    <Text style={styles.foodMeta}>
                      {Math.round(e.calories)} kcal · P{Math.round(e.proteinG)} C{Math.round(e.carbsG)} F{Math.round(e.fatG)}
                    </Text>
                  </View>
                </TouchableOpacity>
              ))}
            </View>
          )}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.bg, padding: 16 },
  totalsCard: { backgroundColor: colors.surface, borderRadius: 12, padding: 16, marginBottom: 12 },
  totalCal: { color: colors.primary, fontSize: 22, fontWeight: '700' },
  macros: { color: colors.textMuted, marginTop: 4 },
  tabs: { flexDirection: 'row', gap: 6, marginBottom: 8 },
  tab: { flex: 1, paddingVertical: 8, borderRadius: 8, backgroundColor: colors.surface, alignItems: 'center' },
  tabActive: { backgroundColor: colors.primary },
  tabText: { color: colors.text, fontSize: 12 },
  tabTextActive: { color: colors.bg, fontWeight: '700' },
  input: {
    backgroundColor: colors.surface, color: colors.text, padding: 12, borderRadius: 8,
    borderWidth: 1, borderColor: colors.border, marginBottom: 12,
  },
  section: { marginBottom: 16 },
  sectionTitle: { color: colors.text, fontWeight: '700', marginBottom: 6 },
  row: {
    flexDirection: 'row', alignItems: 'center', backgroundColor: colors.surface,
    padding: 12, borderRadius: 8, marginBottom: 6, gap: 8,
  },
  foodName: { color: colors.text },
  foodMeta: { color: colors.textMuted, fontSize: 12, marginTop: 2 },
  add: { color: colors.primary, fontSize: 24, fontWeight: '700', paddingHorizontal: 8 },
  muted: { color: colors.textMuted },
});
