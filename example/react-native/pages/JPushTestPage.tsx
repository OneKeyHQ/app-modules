import { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import JPush, {
  DEMO_TEST_APP_KEY,
  DEMO_TEST_PACKAGE_NAME,
} from '@onekeyfe/react-native-jpush';

export function JPushTestPage() {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  const handleInit = () => {
    clearResults();
    try {
      JPush.setLoggerEnable(true);
      JPush.init({
        appKey: DEMO_TEST_APP_KEY,
        channel: 'developer-default',
        production: false,
      });
      setResult({
        initialized: true,
        appKey: DEMO_TEST_APP_KEY,
        packageName: DEMO_TEST_PACKAGE_NAME,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const handleGetRegistrationId = () => {
    clearResults();
    try {
      JPush.getRegistrationID((res) => {
        setResult(res);
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const handleSetAlias = () => {
    clearResults();
    try {
      const sequence = Date.now() % 100000;
      JPush.setAlias({
        sequence,
        alias: 'demo_test_user',
      });
      setResult({ requestedAlias: 'demo_test_user', sequence });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  return (
    <TestPageBase title="JPush / JCore SDK Test">
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>JPush Nitro &amp; Legacy Bridge</Text>
        <TestButton title="Init JPush (Sandbox AppKey)" onPress={handleInit} />
        <TestButton
          title="Get Registration ID"
          onPress={handleGetRegistrationId}
        />
        <TestButton title="Set Alias" onPress={handleSetAlias} />
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
