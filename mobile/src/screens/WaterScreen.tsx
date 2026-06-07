import React from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { waterApi } from '@/api/water';
import { colors } from '@/theme/colors';

const QUICK = [200, 250, 500, 750];

function todayISO() {
  return new Date().toISOString().slice(0, 10);
}

export function WaterScreen() {
  const qc = useQueryClient();
  const date = todayISO();
  const day = useQuery({ queryKey: ['water', 'day', date], queryFn: () => waterApi.day(date) });

  const add = useMutation({
    mutationFn: (ml: number) => waterApi.log(date, ml),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['water', 'day', date] }),
    onError: (e: Error) => Alert.alert('Could not log', e.message),
  });

  const remove = useMutation({
    mutationFn: (id: number) => waterApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['water', 'day', date] }),
  });

  const pct = day.data ? Math.min(100, day.data.progressPct) : 0;

  return (
    <View style={styles.screen}>
      <View style={styles.card}>
        <Text style={styles.label}>Today</Text>
        <Text style={styles.total}>
          {day.data ? day.data.totalMl : 0}<Text style={styles.unit}>ml</Text>
        </Text>
        <Text style={styles.goal}>
          of {day.data?.goalMl ?? 0}ml · {day.data?.progressPct ?? 0}%
        </Text>
        <View style={styles.barTrack}>
          <View style={[styles.barFill, { width: `${pct}%` }]} />
        </View>
      </View>

      <View style={styles.quickRow}>
        {QUICK.map((ml) => (
          <TouchableOpacity key={ml} style={styles.quickBtn} onPress={() => add.mutate(ml)} disabled={add.isPending}>
            <Text style={styles.quickText}>+{ml}ml</Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.h2}>Entries</Text>
      <FlatList
        data={day.data?.entries ?? []}
        keyExtractor={(e) => String(e.id)}
        ListEmptyComponent={<Text style={styles.muted}>No entries yet</Text>}
        renderItem={({ item }) => (
          <TouchableOpacity style={styles.row} onLongPress={() => remove.mutate(item.id)}>
            <Text style={styles.entryAmt}>+{item.amountMl}ml</Text>
            <Text style={styles.muted}>{new Date(item.loggedAt).toLocaleTimeString()}</Text>
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.bg, padding: 16 },
  card: { backgroundColor: colors.surface, padding: 20, borderRadius: 16, marginBottom: 16 },
  label: { color: colors.textMuted, fontSize: 12, textTransform: 'uppercase', letterSpacing: 1 },
  total: { color: colors.accent, fontSize: 48, fontWeight: '800' },
  unit: { fontSize: 18, color: colors.textMuted, fontWeight: '400' },
  goal: { color: colors.textMuted, marginBottom: 10 },
  barTrack: { height: 8, borderRadius: 4, backgroundColor: colors.surfaceAlt, overflow: 'hidden' },
  barFill: { height: '100%', backgroundColor: colors.accent },
  quickRow: { flexDirection: 'row', gap: 8, marginBottom: 16 },
  quickBtn: { flex: 1, backgroundColor: colors.surface, padding: 14, borderRadius: 10, alignItems: 'center', borderWidth: 1, borderColor: colors.border },
  quickText: { color: colors.text, fontWeight: '600' },
  h2: { color: colors.text, fontWeight: '700', marginBottom: 6 },
  row: { backgroundColor: colors.surface, padding: 12, borderRadius: 8, marginBottom: 6, flexDirection: 'row', justifyContent: 'space-between' },
  entryAmt: { color: colors.text },
  muted: { color: colors.textMuted },
});
