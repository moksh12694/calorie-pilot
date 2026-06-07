import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

import { colors } from '@/theme/colors';
import { DashboardScreen } from '@/screens/DashboardScreen';
import { FoodLogScreen } from '@/screens/FoodLogScreen';
import { WaterScreen } from '@/screens/WaterScreen';
import { WeightScreen } from '@/screens/WeightScreen';
import { ProfileScreen } from '@/screens/ProfileScreen';

const Tab = createBottomTabNavigator();

export function AppTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        tabBarStyle: { backgroundColor: colors.surface, borderTopColor: colors.border },
        tabBarActiveTintColor: colors.primary,
        tabBarInactiveTintColor: colors.textMuted,
        headerStyle: { backgroundColor: colors.surface },
        headerTitleStyle: { color: colors.text },
      }}
    >
      <Tab.Screen name="Dashboard" component={DashboardScreen} />
      <Tab.Screen name="Food"      component={FoodLogScreen} />
      <Tab.Screen name="Water"     component={WaterScreen} />
      <Tab.Screen name="Weight"    component={WeightScreen} />
      <Tab.Screen name="Profile"   component={ProfileScreen} />
    </Tab.Navigator>
  );
}
