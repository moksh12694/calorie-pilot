import { useCallback, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { authApi, AuthResponse } from '@/api/auth';
import { AppDispatch, RootState } from '@/store';
import { clearSession, markHydrated, setSession } from '@/store/slices/authSlice';
import { secureStorage } from '@/utils/secureStorage';

export function useAuth() {
  const dispatch = useDispatch<AppDispatch>();
  const auth = useSelector((s: RootState) => s.auth);

  const persist = useCallback(
    async (res: AuthResponse) => {
      await secureStorage.saveSession(res.accessToken, res.refreshToken, res.user);
      dispatch(setSession({ user: res.user, accessToken: res.accessToken, refreshToken: res.refreshToken }));
    },
    [dispatch]
  );

  const login = useCallback(
    async (email: string, password: string) => {
      const res = await authApi.login({ email, password });
      await persist(res);
    },
    [persist]
  );

  const signup = useCallback(
    async (email: string, password: string, name: string) => {
      const res = await authApi.signup({ email, password, name });
      await persist(res);
    },
    [persist]
  );

  const logout = useCallback(async () => {
    await secureStorage.clearSession();
    dispatch(clearSession());
  }, [dispatch]);

  useEffect(() => {
    if (auth.hydrated) return;
    (async () => {
      const stored = await secureStorage.loadSession();
      if (stored) {
        dispatch(setSession(stored));
      } else {
        dispatch(markHydrated());
      }
    })();
  }, [auth.hydrated, dispatch]);

  return {
    user: auth.user,
    accessToken: auth.accessToken,
    hydrated: auth.hydrated,
    isAuthenticated: !!auth.accessToken,
    login,
    signup,
    logout,
  };
}
