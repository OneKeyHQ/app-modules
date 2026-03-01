import { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, Alert } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeAppUpdate } from '@onekeyfe/react-native-app-update';

interface AppUpdateTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function AppUpdateTestPage({ onGoHome, safeAreaInsets }: AppUpdateTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [downloadUrl, setDownloadUrl] = useState('');
  const [filePath, setFilePath] = useState('');
  const listenerIdRef = useRef<number | null>(null);

  useEffect(() => {
    return () => {
      if (listenerIdRef.current !== null) {
        ReactNativeAppUpdate.removeDownloadListener(listenerIdRef.current);
      }
    };
  }, []);

  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  const testClearCache = async () => {
    clearResults();
    try {
      await ReactNativeAppUpdate.clearCache();
      setResult({ cacheCleared: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testDownloadAPK = async () => {
    clearResults();
    if (!downloadUrl) {
      setError('Please enter a download URL');
      return;
    }
    try {
      await ReactNativeAppUpdate.downloadAPK({
        downloadUrl,
        filePath: filePath || 'update.apk',
        notificationTitle: 'Downloading Update',
        fileSize: 0,
      });
      setResult({ downloadStarted: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testVerifyAPK = async () => {
    clearResults();
    if (!downloadUrl || !filePath) {
      setError('Please enter both download URL and file path');
      return;
    }
    try {
      await ReactNativeAppUpdate.verifyAPK({ downloadUrl, filePath });
      setResult({ verified: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const testInstallAPK = async () => {
    if (!downloadUrl || !filePath) {
      setError('Please enter both download URL and file path');
      return;
    }
    Alert.alert(
      'Confirm',
      'This will attempt to install the APK. Continue?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Install',
          onPress: async () => {
            clearResults();
            try {
              await ReactNativeAppUpdate.installAPK({ downloadUrl, filePath });
              setResult({ installTriggered: true });
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
      const id = ReactNativeAppUpdate.addDownloadListener((event) => {
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
      title="App Update Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      {/* Download Parameters */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>APK Download & Install (Android)</Text>
        <TestInput placeholder="Download URL" value={downloadUrl} onChangeText={setDownloadUrl} />
        <TestInput placeholder="File Path" value={filePath} onChangeText={setFilePath} />
        <TestButton title="Download APK" onPress={testDownloadAPK} />
        <TestButton title="Verify APK" onPress={testVerifyAPK} />
        <TestButton title="Install APK" onPress={testInstallAPK} />
      </View>

      {/* Cache & Events */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Cache & Events</Text>
        <TestButton title="Clear Cache" onPress={testClearCache} />
        <TestButton title="Register Download Listener" onPress={testAddDownloadListener} />
      </View>

      <TestResult result={result} error={error} />

      <View style={styles.instructionsContainer}>
        <Text style={styles.instructionsTitle}>Instructions:</Text>
        <Text style={styles.instructionsText}>
          • This module handles APK downloads and installs (Android only){'\n'}
          • On iOS, all operations are no-ops{'\n'}
          • Register a download listener to track progress events
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
