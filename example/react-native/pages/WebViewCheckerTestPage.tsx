import { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import { ReactNativeWebviewChecker } from '@onekeyfe/react-native-webview-checker';

interface WebViewCheckerTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function WebViewCheckerTestPage({ onGoHome, safeAreaInsets }: WebViewCheckerTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  const testGetWebViewPackageInfo = async () => {
    clearResults();
    try {
      const info = await ReactNativeWebviewChecker.getCurrentWebViewPackageInfo();
      setResult({ webViewPackageInfo: info });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testIsGooglePlayServicesAvailable = async () => {
    clearResults();
    try {
      const status = await ReactNativeWebviewChecker.isGooglePlayServicesAvailable();
      setResult({ googlePlayServices: status });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testAll = async () => {
    clearResults();
    try {
      const [webViewInfo, gpsStatus] = await Promise.all([
        ReactNativeWebviewChecker.getCurrentWebViewPackageInfo(),
        ReactNativeWebviewChecker.isGooglePlayServicesAvailable(),
      ]);
      setResult({
        webViewPackageInfo: webViewInfo,
        googlePlayServices: gpsStatus,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  return (
    <TestPageBase
      title="WebView Checker Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>WebView Info (Android)</Text>

        <TestButton title="Get WebView Package Info" onPress={testGetWebViewPackageInfo} />
        <TestButton title="Check Google Play Services" onPress={testIsGooglePlayServicesAvailable} />
        <TestButton title="Get All Info" onPress={testAll} />
      </View>

      <TestResult result={result} error={error} />

      <View style={styles.instructionsContainer}>
        <Text style={styles.instructionsTitle}>Instructions:</Text>
        <Text style={styles.instructionsText}>
          • WebView Package Info: shows installed WebView version (Android){'\n'}
          • Google Play Services: checks availability status{'\n'}
          • On iOS, these return stub/empty values
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
