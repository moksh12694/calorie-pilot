import { secureStorage } from '@/utils/secureStorage';

// Mock expo-secure-store with an in-mockMemory map
const mockMemory = new Map<string, string>();
jest.mock('expo-secure-store', () => ({
  setItemAsync: jest.fn(async (k: string, v: string) => { mockMemory.set(k, v); }),
  getItemAsync: jest.fn(async (k: string) => mockMemory.get(k) ?? null),
  deleteItemAsync: jest.fn(async (k: string) => { mockMemory.delete(k); }),
}));

describe('secureStorage', () => {
  beforeEach(() => mockMemory.clear());

  it('round-trips a session', async () => {
    await secureStorage.saveSession('A', 'R', { id: 'u', email: 'a@b.c', name: 'A' });
    const loaded = await secureStorage.loadSession();
    expect(loaded?.accessToken).toBe('A');
    expect(loaded?.refreshToken).toBe('R');
    expect(loaded?.user.email).toBe('a@b.c');
  });

  it('clears on logout', async () => {
    await secureStorage.saveSession('A', 'R', {});
    await secureStorage.clearSession();
    expect(await secureStorage.loadSession()).toBeNull();
  });
});
