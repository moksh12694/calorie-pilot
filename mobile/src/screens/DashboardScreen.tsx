import React from 'react';
import { ScrollView, Text, View, StyleSheet, RefreshControl } from 'react-native';
import { useQuery, useQueryClient } from '@tanstack/react-query';

import { stepsApi } from '@/api/steps';
import { StepCard } from '@/components/StepCard';
import { usePedometer } from '@/hooks/usePedometer';
import { usePushRegistration } from '@/hooks/usePushRegistration';
import { colors } from '@/theme/colors';

export function DashboardScreen() {
  const qc = useQueryClient();
  const { data, isLoading, refetch, isRefetching } = useQuery({
    queryKey: ['steps', 'today'],
    queryFn: stepsApi.today,
    refetchInterval: 30_000,
  });

  // Side effect: pedometer updates fire stepsApi.sync; we invalidate after each tick.
  usePedometer({ enabled: true });
  usePushRegistration(true);

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.content}
      refreshControl={
        <RefreshControl
          tintColor={colors.primary}
          refreshing={isRefetching}
          onRefresh={() => { refetch(); qc.invalidateQueries({ queryKey: ['steps'] }); }}
        />
      }
    >
      <Text style={styles.h1}>Today</Text>
      {isLoading || !data ? (
        <View style={styles.placeholder}><Text style={styles.muted}>Loading…</Text></View>
      ) : (
        <StepCard steps={data.steps} goal={data.goal} progressPct={data.progressPct} />
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { backgroundColor: colors.bg, flex: 1 },
  content: { padding: 16, gap: 16 },
  h1: { color: colors.text, fontSize: 28, fontWeight: '700' },
  placeholder: { padding: 32, alignItems: 'center', backgroundColor: colors.surface, borderRadius: 16 },
  muted: { color: colors.textMuted },
});
