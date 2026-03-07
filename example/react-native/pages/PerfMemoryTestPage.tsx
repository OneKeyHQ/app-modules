import { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import { ReactNativePerfMemory } from '@onekeyfe/react-native-perf-memory';

export function PerfMemoryTestPage() {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isPolling, setIsPolling] = useState(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, []);

  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  const testGetMemoryUsage = async () => {
    clearResults();
    try {
      const memory = await ReactNativePerfMemory.getMemoryUsage();
      setResult({
        rss: memory.rss,
        rssMB: `${(memory.rss / 1024 / 1024).toFixed(2)} MB`,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const togglePolling = () => {
    if (isPolling) {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
      setIsPolling(false);
    } else {
      setIsPolling(true);
      const poll = async () => {
        try {
          const memory = await ReactNativePerfMemory.getMemoryUsage();
          setResult({
            rss: memory.rss,
            rssMB: `${(memory.rss / 1024 / 1024).toFixed(2)} MB`,
            timestamp: new Date().toLocaleTimeString(),
          });
        } catch (err) {
          setError(err instanceof Error ? err.message : 'Unknown error');
        }
      };
      void poll();
      intervalRef.current = setInterval(() => void poll(), 1000);
    }
  };

  return (
    <TestPageBase
      title="Perf Memory Test"
    >
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Memory Usage</Text>

        <TestButton
          title="Get Memory Usage (Once)"
          onPress={testGetMemoryUsage}
        />

        <TestButton
          title={isPolling ? 'Stop Polling' : 'Start Polling (1s)'}
          onPress={togglePolling}
        />
      </View>

      <TestResult result={result} error={error} />

      <View style={styles.instructionsContainer}>
        <Text style={styles.instructionsTitle}>Instructions:</Text>
        <Text style={styles.instructionsText}>
          • Get Memory Usage: reads the process RSS (Resident Set Size){'\n'}
          • Polling: continuously reads memory every second{'\n'}
          • RSS is reported in bytes and converted to MB
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
