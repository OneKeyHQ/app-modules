import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { NativeListAccountSelectorPage } from '../pages/NativeListAccountSelectorPage';

import './styles.css';

const rootElement = document.getElementById('root');

if (!rootElement) throw new Error('Missing #root element');

createRoot(rootElement).render(
  <StrictMode>
    <SafeAreaProvider>
      <NavigationContainer>
        <NativeListAccountSelectorPage />
      </NavigationContainer>
    </SafeAreaProvider>
  </StrictMode>,
);
