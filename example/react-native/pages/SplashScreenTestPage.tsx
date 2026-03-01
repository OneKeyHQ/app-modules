import { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import { ReactNativeSplashScreen } from '@onekeyfe/react-native-splash-screen';

interface SplashScreenTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function SplashScreenTestPage({ onGoHome, safeAreaInsets }: SplashScreenTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  const testPreventAutoHide = async () => {
    clearResults();
    try {
      const ok = await ReactNativeSplashScreen.preventAutoHideAsync();
      setResult({ preventAutoHide: ok });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testHide = async () => {
    clearResults();
    try {
      const ok = await ReactNativeSplashScreen.hideAsync();
      setResult({ hide: ok });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  return (
    <TestPageBase
      title="Splash Screen Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Legacy Splash Screen (Android &lt; 12)</Text>

        <TestButton title="Prevent Auto Hide" onPress={testPreventAutoHide} />
        <TestButton title="Hide Splash Screen" onPress={testHide} />
      </View>

      <TestResult result={result} error={error} />

      <View style={styles.instructionsContainer}>
        <Text style={styles.instructionsTitle}>Instructions:</Text>
        <Text style={styles.instructionsText}>
          • preventAutoHideAsync: keeps the splash screen visible{'\n'}
          • hideAsync: hides the splash screen{'\n'}
          • Only active on Android &lt; 12 (older devices){'\n'}
          • On iOS, both methods return true (no-op)
        </Text>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  section: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 10,
  },
  instructionsContainer: {
    backgroundColor: '#fff3cd',
    padding: 15,
    borderRadius: 8,
    marginTop: 20,
    borderLeftWidth: 4,
    borderLeftColor: '#ffc107',
  },
  instructionsTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#856404',
    marginBottom: 8,
  },
  instructionsText: {
    fontSize: 14,
    color: '#856404',
    lineHeight: 20,
  },
});
