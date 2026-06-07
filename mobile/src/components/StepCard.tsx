import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

import { colors } from '@/theme/colors';

interface Props {
  steps: number;
  goal: number;
  progressPct: number;
}

export function StepCard({ steps, goal, progressPct }: Props) {
  const clamped = Math.min(100, progressPct);
  return (
    <View style={styles.card}>
      <Text style={styles.label}>Today's steps</Text>
      <Text style={styles.steps}>{steps.toLocaleString()}</Text>
      <Text style={styles.goal}>of {goal.toLocaleString()} goal · {progressPct}%</Text>
      <View style={styles.barTrack}>
        <View style={[styles.barFill, { width: `${clamped}%` }]} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 20,
    borderWidth: 1,
    borderColor: colors.border,
  },
  label: { color: colors.textMuted, fontSize: 13, textTransform: 'uppercase', letterSpacing: 1 },
  steps: { color: colors.primary, fontSize: 44, fontWeight: '800', marginTop: 4 },
  goal:  { color: colors.textMuted, marginBottom: 12 },
  barTrack: { height: 8, borderRadius: 4, backgroundColor: colors.surfaceAlt, overflow: 'hidden' },
  barFill:  { height: '100%', backgroundColor: colors.primary },
});
