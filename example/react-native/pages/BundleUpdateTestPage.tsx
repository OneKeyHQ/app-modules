import { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, Alert } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeBundleUpdate } from '@onekeyfe/react-native-bundle-update';

interface BundleUpdateTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function BundleUpdateTestPage({ onGoHome, safeAreaInsets }: BundleUpdateTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [appVersion, setAppVersion] = useState('');
  const [bundleVersion, setBundleVersion] = useState('');
  const listenerIdRef = useRef<number | null>(null);

  useEffect(() => {
    return () => {
      if (listenerIdRef.current !== null) {
        ReactNativeBundleUpdate.removeDownloadListener(listenerIdRef.current);
      }
    };
  }, []);

  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  const testGetWebEmbedPath = () => {
    clearResults();
    try {
      const path = ReactNativeBundleUpdate.getWebEmbedPath();
      setResult({ webEmbedPath: path || '(empty)' });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testGetWebEmbedPathAsync = async () => {
    clearResults();
    try {
      const path = await ReactNativeBundleUpdate.getWebEmbedPathAsync();
      setResult({ webEmbedPathAsync: path || '(empty)' });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testGetJsBundlePath = async () => {
    clearResults();
    try {
      const path = await ReactNativeBundleUpdate.getJsBundlePath();
      setResult({ jsBundlePath: path || '(empty)' });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testGetNativeAppVersion = async () => {
    clearResults();
    try {
      const version = await ReactNativeBundleUpdate.getNativeAppVersion();
      setResult({ nativeAppVersion: version });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testGetFallbackBundleData = async () => {
    clearResults();
    try {
      const data = await ReactNativeBundleUpdate.getFallbackUpdateBundleData();
      setResult({ fallbackBundleData: data });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testListLocalBundles = async () => {
    clearResults();
    try {
      const bundles = await ReactNativeBundleUpdate.listLocalBundles();
      setResult({ localBundles: bundles });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testIsBundleExists = async () => {
    clearResults();
    if (!appVersion || !bundleVersion) {
      setError('Please enter both app version and bundle version');
      return;
    }
    try {
      const exists = await ReactNativeBundleUpdate.isBundleExists(appVersion, bundleVersion);
      setResult({ bundleExists: exists, appVersion, bundleVersion });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testVerification = async () => {
    clearResults();
    try {
      const ok = await ReactNativeBundleUpdate.testVerification();
      setResult({ verificationTest: ok });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testClearBundle = async () => {
    clearResults();
    try {
      await ReactNativeBundleUpdate.clearBundle();
      setResult({ cleared: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testClearAllJSBundleData = async () => {
    Alert.alert(
      'Confirm',
      'This will clear ALL JS bundle data. Continue?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Clear All',
          style: 'destructive',
          onPress: async () => {
            clearResults();
            try {
              const r = await ReactNativeBundleUpdate.clearAllJSBundleData();
              setResult({ clearAll: r });
            } catch (err) {
              setError(err instanceof Error ? err.message : 'Unknown error');
            }
          },
        },
      ],
    );
  };

  const testAddDownloadListener = () => {
    clearResults();
    try {
      if (listenerIdRef.current !== null) {
        Alert.alert('Info', 'Listener already registered');
        return;
      }
      const id = ReactNativeBundleUpdate.addDownloadListener((event) => {
        setResult({
          downloadEvent: event,
          timestamp: new Date().toLocaleTimeString(),
        });
      });
      listenerIdRef.current = id;
      setResult({ listenerRegistered: true, listenerId: id });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  return (
    <TestPageBase
      title="Bundle Update Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      {/* Path & Version Info */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Paths & Info</Text>
        <TestButton title="Get Web Embed Path (sync)" onPress={testGetWebEmbedPath} />
        <TestButton title="Get Web Embed Path (async)" onPress={testGetWebEmbedPathAsync} />
        <TestButton title="Get JS Bundle Path" onPress={testGetJsBundlePath} />
        <TestButton title="Get Native App Version" onPress={testGetNativeAppVersion} />
      </View>

      {/* Bundle Data */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Bundle Data</Text>
        <TestButton title="Get Fallback Bundle Data" onPress={testGetFallbackBundleData} />
        <TestButton title="List Local Bundles" onPress={testListLocalBundles} />
      </View>

      {/* Bundle Exists Check */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Check Bundle Exists</Text>
        <TestInput placeholder="App Version" value={appVersion} onChangeText={setAppVersion} />
        <TestInput placeholder="Bundle Version" value={bundleVersion} onChangeText={setBundleVersion} />
        <TestButton title="Check Bundle Exists" onPress={testIsBundleExists} />
      </View>

      {/* Verification & Cleanup */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Verification & Cleanup</Text>
        <TestButton title="Test Verification" onPress={testVerification} />
        <TestButton title="Clear Bundle" onPress={testClearBundle} />
        <TestButton title="Clear All JS Bundle Data" onPress={testClearAllJSBundleData} />
      </View>

      {/* Events */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Download Events</Text>
        <TestButton title="Register Download Listener" onPress={testAddDownloadListener} />
      </View>

      <TestResult result={result} error={error} />
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
});
