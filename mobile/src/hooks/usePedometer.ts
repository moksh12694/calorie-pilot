import { useEffect, useRef, useState } from 'react';
import { Pedometer } from 'expo-sensors';

import { stepsApi } from '@/api/steps';

function todayIsoDate(): string {
  return new Date().toISOString().slice(0, 10);
}

function startOfDay(): Date {
  const d = new Date();
  d.setHours(0, 0, 0, 0);
  return d;
}

/**
 * Reads today's step count from Expo Pedometer and pushes monotonic updates to the backend.
 * - Live subscription via Pedometer.watchStepCount delivers deltas — we add them to today's baseline.
 * - The baseline is the cumulative count from midnight (Pedometer.getStepCountAsync).
 */
export function usePedometer(opts: { enabled: boolean; syncIntervalMs?: number } = { enabled: true }) {
  const { enabled, syncIntervalMs = 30_000 } = opts;
  const [available, setAvailable] = useState<boolean | null>(null);
  const [steps, setSteps] = useState(0);
  const baselineRef = useRef(0);
  const lastSyncedRef = useRef(0);

  // Detect availability + read today's baseline.
  useEffect(() => {
    let cancelled = false;
    (async () => {
      const ok = await Pedometer.isAvailableAsync();
      if (cancelled) return;
      setAvailable(ok);
      if (!ok || !enabled) return;
      try {
        const { steps: todaySoFar } = await Pedometer.getStepCountAsync(startOfDay(), new Date());
        baselineRef.current = todaySoFar;
        setSteps(todaySoFar);
      } catch {
        // permission denied or unsupported on emulator
      }
    })();
    return () => { cancelled = true; };
  }, [enabled]);

  // Live subscription — sum the delta since subscription start onto the baseline.
  useEffect(() => {
    if (!enabled || !available) return;
    const subscription = Pedometer.watchStepCount(({ steps: delta }) => {
      setSteps(baselineRef.current + delta);
    });
    return () => subscription.remove();
  }, [enabled, available]);

  // Push updates to the backend on a short interval (skip no-ops).
  useEffect(() => {
    if (!enabled || !available) return;
    const id = setInterval(() => {
      if (steps === lastSyncedRef.current) return;
      lastSyncedRef.current = steps;
      stepsApi.sync({ date: todayIsoDate(), steps }).catch(() => {/* swallow; retried next tick */});
    }, syncIntervalMs);
    return () => clearInterval(id);
  }, [enabled, available, steps, syncIntervalMs]);

  return { available, steps };
}
