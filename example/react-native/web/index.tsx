import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { NativeListTokenSelectorPage } from '../pages/NativeListTokenSelectorPage';

import './styles.css';

const rootElement = document.getElementById('root');

if (!rootElement) throw new Error('Missing #root element');

createRoot(rootElement).render(
  <StrictMode>
    <SafeAreaProvider>
      <NavigationContainer>
        <NativeListTokenSelectorPage />
      </NavigationContainer>
    </SafeAreaProvider>
  </StrictMode>,
);
