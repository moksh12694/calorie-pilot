import React, { useState } from 'react';

import { LoginScreen } from '@/screens/LoginScreen';
import { SignupScreen } from '@/screens/SignupScreen';

export function AuthNavigator() {
  const [mode, setMode] = useState<'login' | 'signup'>('login');
  return mode === 'login'
    ? <LoginScreen onSwitchToSignup={() => setMode('signup')} />
    : <SignupScreen onSwitchToLogin={() => setMode('login')} />;
}
