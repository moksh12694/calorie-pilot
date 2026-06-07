import React, { useMemo, useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView, Alert, Dimensions } from 'react-native';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { LineChart } from 'react-native-chart-kit';

import { weightApi } from '@/api/weight';
import { colors } from '@/theme/colors';

const screenW = Dimensions.get('window').width;

function isoDate(d: Date) {
  return d.toISOString().slice(0, 10);
}

export function WeightScreen() {
  const qc = useQueryClient();
  const today = new Date();
  const from = isoDate(new Date(today.getFullYear(), today.getMonth() - 2, today.getDate()));
  const to = isoDate(today);

  const history = useQuery({
    queryKey: ['weight', 'history', from, to],
    queryFn: () => weightApi.history(from, to),
  });

  const [input, setInput] = useState('');

  const add = useMutation({
    mutationFn: (kg: number) => weightApi.log({ date: isoDate(new Date()), weightKg: kg }),
    onSuccess: () => {
      setInput('');
      qc.invalidateQueries({ queryKey: ['weight'] });
    },
    onError: (e: Error) => Alert.alert('Could not log', e.message),
  });

  const chartData = useMemo(() => {
    const e = history.data?.entries ?? [];
    return {
      labels: e.map((x) => x.date.slice(5)),
      datasets: [{ data: e.length ? e.map((x) => Number(x.weightKg)) : [0] }],
    };
  }, [history.data]);

  const onSubmit = () => {
    const n = parseFloat(input.replace(',', '.'));
    if (!Number.isFinite(n) || n <= 0) {
      Alert.alert('Invalid', 'Enter a number, e.g. 72.5');
      return;
    }
    add.mutate(n);
  };

  return (
    <ScrollView style={styles.screen} contentContainerStyle={{ padding: 16 }}>
      <View style={styles.card}>
        <Text style={styles.label}>Current</Text>
        <Text style={styles.weight}>
          {history.data?.latestKg ? `${history.data.latestKg} kg` : '—'}
        </Text>
        {history.data?.totalDeltaKg != null && (
          <Text style={styles.delta}>
            {history.data.totalDeltaKg >= 0 ? '▲' : '▼'} {Math.abs(history.data.totalDeltaKg).toFixed(1)} kg since start
          </Text>
        )}
      </View>

      <View style={styles.logRow}>
        <TextInput
          style={styles.input}
          placeholder="Today's weight (kg)"
          placeholderTextColor={colors.textMuted}
          keyboardType="decimal-pad"
          value={input}
          onChangeText={setInput}
        />
        <TouchableOpacity style={styles.button} onPress={onSubmit} disabled={add.isPending}>
          <Text style={styles.buttonText}>{add.isPending ? '…' : 'Log'}</Text>
        </TouchableOpacity>
      </View>

      {(history.data?.entries?.length ?? 0) > 1 && (
        <View style={styles.card}>
          <Text style={styles.h2}>Trend</Text>
          <LineChart
            data={chartData}
            width={screenW - 64}
            height={180}
            withInnerLines={false}
            withOuterLines={false}
            yAxisSuffix="kg"
            chartConfig={{
              backgroundGradientFrom: colors.surface,
              backgroundGradientTo: colors.surface,
              decimalPlaces: 1,
              color: () => colors.primary,
              labelColor: () => colors.textMuted,
              propsForDots: { r: '3' },
            }}
            bezier
            style={{ borderRadius: 12 }}
          />
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.bg },
  card: { backgroundColor: colors.surface, borderRadius: 16, padding: 16, marginBottom: 16 },
  label: { color: colors.textMuted, fontSize: 12, textTransform: 'uppercase', letterSpacing: 1 },
  weight: { color: colors.primary, fontSize: 36, fontWeight: '800', marginTop: 4 },
  delta: { color: colors.textMuted, marginTop: 4 },
  logRow: { flexDirection: 'row', gap: 8, marginBottom: 16 },
  input: {
    flex: 1, backgroundColor: colors.surface, color: colors.text, padding: 12, borderRadius: 8,
    borderWidth: 1, borderColor: colors.border,
  },
  button: { backgroundColor: colors.primary, paddingHorizontal: 20, justifyContent: 'center', borderRadius: 8 },
  buttonText: { color: colors.bg, fontWeight: '700' },
  h2: { color: colors.text, fontWeight: '700', marginBottom: 8 },
});
