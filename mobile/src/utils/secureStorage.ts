import * as SecureStore from 'expo-secure-store';

const KEYS = {
  accessToken: 'cp_access_token',
  refreshToken: 'cp_refresh_token',
  user: 'cp_user',
} as const;

export const secureStorage = {
  async saveSession(accessToken: string, refreshToken: string, user: object) {
    await SecureStore.setItemAsync(KEYS.accessToken, accessToken);
    await SecureStore.setItemAsync(KEYS.refreshToken, refreshToken);
    await SecureStore.setItemAsync(KEYS.user, JSON.stringify(user));
  },

  async loadSession() {
    const [accessToken, refreshToken, rawUser] = await Promise.all([
      SecureStore.getItemAsync(KEYS.accessToken),
      SecureStore.getItemAsync(KEYS.refreshToken),
      SecureStore.getItemAsync(KEYS.user),
    ]);
    if (!accessToken || !refreshToken || !rawUser) return null;
    return { accessToken, refreshToken, user: JSON.parse(rawUser) };
  },

  async clearSession() {
    await Promise.all([
      SecureStore.deleteItemAsync(KEYS.accessToken),
      SecureStore.deleteItemAsync(KEYS.refreshToken),
      SecureStore.deleteItemAsync(KEYS.user),
    ]);
  },
};
