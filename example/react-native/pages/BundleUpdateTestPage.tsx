import { useState, useRef, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, Alert, Switch } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeBundleUpdate } from '@onekeyfe/react-native-bundle-update';
import { createMMKV } from 'react-native-mmkv';

const devSettingsMmkv = createMMKV({ id: 'onekey-app-setting' });
const KEY_DEV_MODE = 'onekey_developer_mode_enabled';
const KEY_SKIP_GPG = 'onekey_bundle_skip_gpg_verification';

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

  const [devModeEnabled, setDevModeEnabled] = useState(
    () => devSettingsMmkv.getBoolean(KEY_DEV_MODE) ?? false,
  );
  const [skipGPGEnabled, setSkipGPGEnabled] = useState(
    () => devSettingsMmkv.getBoolean(KEY_SKIP_GPG) ?? false,
  );

  const toggleDevMode = useCallback((value: boolean) => {
    devSettingsMmkv.set(KEY_DEV_MODE, value);
    setDevModeEnabled(value);
  }, []);

  const toggleSkipGPG = useCallback((value: boolean) => {
    devSettingsMmkv.set(KEY_SKIP_GPG, value);
    setSkipGPGEnabled(value);
  }, []);

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

  // DevSettings + skip-GPG: verify all local bundles to exercise the GPG check path.
  // Native code logs: "GPG check: devSettings=X, skipGPGToggle=Y, skipGPG=Z"
  // To enable skip: set both MMKV keys to true in instance "onekey-app-setting":
  //   onekey_developer_mode_enabled = true
  //   onekey_bundle_skip_gpg_verification = true
  const testVerifyAllLocalBundles = async () => {
    clearResults();
    try {
      const bundles = await ReactNativeBundleUpdate.listLocalBundles();
      if (bundles.length === 0) {
        setResult({ message: 'No local bundles found', bundles: [] });
        return;
      }
      const results: Record<string, string> = {};
      for (const b of bundles) {
        try {
          await ReactNativeBundleUpdate.verifyExtractedBundle(b.appVersion, b.bundleVersion);
          results[`${b.appVersion}-${b.bundleVersion}`] = 'OK';
        } catch (err) {
          results[`${b.appVersion}-${b.bundleVersion}`] =
            err instanceof Error ? err.message : 'error';
        }
      }
      setResult({ verifyResults: results });
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

      {/* DevSettings Skip-GPG Test */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>DevSettings Skip-GPG Test</Text>
        <View style={styles.toggleRow}>
          <Text style={styles.toggleLabel}>
            {'Developer Mode\n'}
            <Text style={styles.toggleKey}>{KEY_DEV_MODE}</Text>
          </Text>
          <Switch value={devModeEnabled} onValueChange={toggleDevMode} />
        </View>
        <View style={styles.toggleRow}>
          <Text style={styles.toggleLabel}>
            {'Skip GPG Verification\n'}
            <Text style={styles.toggleKey}>{KEY_SKIP_GPG}</Text>
          </Text>
          <Switch value={skipGPGEnabled} onValueChange={toggleSkipGPG} />
        </View>
        <Text style={styles.infoText}>
          {`Both switches must be ON to skip GPG.\nCheck native logs for:\n  GPG check: devSettings=X, skipGPGToggle=Y, skipGPG=Z`}
        </Text>
        <TestButton
          title="Verify All Local Bundles (triggers GPG check)"
          onPress={testVerifyAllLocalBundles}
        />
        <TestButton title="Test Verification (GPG self-test)" onPress={testVerification} />
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
  infoText: {
    fontSize: 12,
    color: '#666',
    fontFamily: 'monospace',
    backgroundColor: '#f5f5f5',
    padding: 8,
    borderRadius: 4,
    marginBottom: 8,
  },
  toggleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#e0e0e0',
    marginBottom: 8,
  },
  toggleLabel: {
    fontSize: 14,
    color: '#333',
    flex: 1,
    marginRight: 12,
  },
  toggleKey: {
    fontSize: 11,
    color: '#888',
    fontFamily: 'monospace',
  },
});
