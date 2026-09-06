import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { NativeListAccountSelectorPage } from '../pages/NativeListAccountSelectorPage';

import './styles.css';

const rootElement = document.getElementById('root');

if (!rootElement) throw new Error('Missing #root element');

const searchParams = new URLSearchParams(window.location.search);
const readTargetNumber = (name: string): number | undefined => {
  const value = searchParams.get(name);
  return value === null ? undefined : Number(value);
};
const initialTarget = {
  walletNumber: readTargetNumber('wallet'),
  accountNumber: readTargetNumber('account'),
};

createRoot(rootElement).render(
  <StrictMode>
    <SafeAreaProvider>
      <NavigationContainer>
        <NativeListAccountSelectorPage initialTarget={initialTarget} />
      </NavigationContainer>
    </SafeAreaProvider>
  </StrictMode>,
);
