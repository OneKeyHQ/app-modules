import { lazy, StrictMode, Suspense } from 'react';
import { createRoot } from 'react-dom/client';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { NativeListAccountSelectorPage } from '../pages/NativeListAccountSelectorPage';

import './styles.css';

const MarketNativePagerExamplePage = lazy(async () => {
  const marketModule = await import('../pages/MarketNativePagerExamplePage');
  return { default: marketModule.MarketNativePagerExamplePage };
});

const marketLoadingFallback = (
  <div className="market-loading" role="status" aria-label="Loading market">
    <span className="market-loading-indicator" />
  </div>
);

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
const page = searchParams.get('page');

createRoot(rootElement).render(
  <StrictMode>
    <SafeAreaProvider>
      <NavigationContainer>
        {page === 'market' ? (
          <Suspense fallback={marketLoadingFallback}>
            <MarketNativePagerExamplePage />
          </Suspense>
        ) : (
          <NativeListAccountSelectorPage initialTarget={initialTarget} />
        )}
      </NavigationContainer>
    </SafeAreaProvider>
  </StrictMode>,
);
