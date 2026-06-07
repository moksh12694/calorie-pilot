import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, FlatList } from 'react-native';
import { useQuery } from '@tanstack/react-query';

import { achievementsApi } from '@/api/achievements';
import { useAuth } from '@/hooks/useAuth';
import { colors } from '@/theme/colors';

export function ProfileScreen() {
  const { user, logout } = useAuth();

  const streak = useQuery({ queryKey: ['achievements', 'streak'], queryFn: achievementsApi.streak });
  const badges = useQuery({ queryKey: ['achievements', 'list'], queryFn: achievementsApi.list });

  return (
    <View style={styles.screen}>
      <View style={styles.headerCard}>
        <Text style={styles.name}>{user?.name ?? '—'}</Text>
        <Text style={styles.email}>{user?.email}</Text>

        <View style={styles.streakRow}>
          <View style={styles.streakBox}>
            <Text style={styles.streakNum}>{streak.data?.currentDays ?? 0}</Text>
            <Text style={styles.streakLabel}>Current streak</Text>
          </View>
          <View style={styles.streakBox}>
            <Text style={styles.streakNum}>{streak.data?.longestDays ?? 0}</Text>
            <Text style={styles.streakLabel}>Longest</Text>
          </View>
        </View>
      </View>

      <Text style={styles.h2}>Achievements</Text>
      <FlatList
        data={badges.data ?? []}
        keyExtractor={(a) => String(a.id)}
        numColumns={3}
        columnWrapperStyle={{ gap: 8 }}
        contentContainerStyle={{ gap: 8 }}
        renderItem={({ item }) => (
          <View style={[styles.badge, !item.earned && styles.badgeLocked]}>
            <Text style={styles.badgeIcon}>{badgeEmoji(item.icon)}</Text>
            <Text style={styles.badgeTitle} numberOfLines={2}>{item.title}</Text>
            {!item.earned && <Text style={styles.badgeLockedHint}>Locked</Text>}
          </View>
        )}
      />

      <TouchableOpacity style={styles.logout} onPress={logout}>
        <Text style={styles.logoutText}>Sign out</Text>
      </TouchableOpacity>
    </View>
  );
}

function badgeEmoji(icon: string | null): string {
  switch (icon) {
    case 'footprints': return '👟';
    case 'medal':      return '🏅';
    case 'flame':      return '🔥';
    case 'droplet':    return '💧';
    case 'utensils':   return '🍽️';
    case 'scale':      return '⚖️';
    default:           return '🏆';
  }
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.bg, padding: 16 },
  headerCard: { backgroundColor: colors.surface, borderRadius: 16, padding: 16, marginBottom: 16 },
  name: { color: colors.text, fontSize: 22, fontWeight: '700' },
  email: { color: colors.textMuted, marginBottom: 16 },
  streakRow: { flexDirection: 'row', gap: 12 },
  streakBox: { flex: 1, backgroundColor: colors.surfaceAlt, padding: 12, borderRadius: 10 },
  streakNum: { color: colors.primary, fontSize: 24, fontWeight: '800' },
  streakLabel: { color: colors.textMuted, fontSize: 12 },
  h2: { color: colors.text, fontWeight: '700', marginBottom: 8 },
  badge: {
    flex: 1, backgroundColor: colors.surface, borderRadius: 12, padding: 10, alignItems: 'center',
    borderWidth: 1, borderColor: colors.border, minHeight: 100,
  },
  badgeLocked: { opacity: 0.45 },
  badgeIcon: { fontSize: 28, marginBottom: 4 },
  badgeTitle: { color: colors.text, fontSize: 11, textAlign: 'center' },
  badgeLockedHint: { color: colors.textMuted, fontSize: 10, marginTop: 4 },
  logout: { marginTop: 24, alignSelf: 'center', padding: 12 },
  logoutText: { color: colors.danger, fontWeight: '600' },
});
