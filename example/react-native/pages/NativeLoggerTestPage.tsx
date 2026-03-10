import React, { useCallback, useEffect, useState } from 'react';
import { View, Text, Alert, StyleSheet, Clipboard, TouchableOpacity } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { NativeLogger } from '@onekeyfe/react-native-native-logger';

const LogLevel = {
  DEBUG: 0,
  INFO: 1,
  WARN: 2,
  ERROR: 3,
} as const;

export function NativeLoggerTestPage() {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [logFiles, setLogFiles] = useState<string[]>([]);
  const [logDir, setLogDir] = useState<string>('');
  const [customMessage, setCustomMessage] = useState('Hello from NativeLogger');

  const refreshLogFiles = useCallback(async () => {
    try {
      const paths = await NativeLogger.getLogFilePaths();
      setLogFiles(paths);
    } catch (err) {
      const msg = err instanceof Error ? err.message : String(err);
      setError(msg);
    }
  }, []);

  useEffect(() => {
    try {
      setLogDir(NativeLogger.getLogDirectory());
    } catch (_) {
      // ignore
    }
    refreshLogFiles();
  }, [refreshLogFiles]);

  const executeOperation = async (operation: () => Promise<any> | any, operationName: string) => {
    setIsLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await Promise.resolve(operation());
      setResult(res);
    } catch (err) {
      const msg = err instanceof Error ? err.message : String(err);
      setError(msg);
      Alert.alert('Error', `${operationName} failed: ${msg}`);
    } finally {
      setIsLoading(false);
    }
  };

  const writeAllLevels = async () => {
    NativeLogger.write(LogLevel.DEBUG, '[Test] Debug message');
    NativeLogger.write(LogLevel.INFO, '[Test] Info message');
    NativeLogger.write(LogLevel.WARN, '[Test] Warning message');
    NativeLogger.write(LogLevel.ERROR, '[Test] Error message');
    setResult('Wrote 4 log messages (debug, info, warn, error)');
    await refreshLogFiles();
  };

  const writeCustomMessage = async () => {
    if (!customMessage.trim()) {
      Alert.alert('Error', 'Please enter a message');
      return;
    }
    NativeLogger.write(LogLevel.INFO, customMessage);
    setResult(`Wrote: ${customMessage}`);
    await refreshLogFiles();
  };

  const writeBatchLogs = async () => {
    const count = 50;
    for (let i = 0; i < count; i++) {
      NativeLogger.write(LogLevel.INFO, `[Batch] Message ${i + 1}/${count}`);
    }
    setResult(`Wrote ${count} batch log messages`);
    await refreshLogFiles();
  };

  const getLogFilePaths = () => {
    executeOperation(async () => {
      const paths = await NativeLogger.getLogFilePaths();
      setLogFiles(paths);
      return {
        count: paths.length,
        directory: logDir || 'N/A',
        files: paths,
      };
    }, 'Get Log File Paths');
  };

  const deleteLogFiles = () => {
    Alert.alert('Confirm', 'Delete all log files?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: () => {
          executeOperation(async () => {
            await NativeLogger.deleteLogFiles();
            await refreshLogFiles();
            return 'All log files deleted';
          }, 'Delete Log Files');
        },
      },
    ]);
  };

  return (
    <TestPageBase title="Native Logger Test">
      {/* Log Directory */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Log Directory</Text>
        <View style={styles.dirBox}>
          <View style={styles.dirHeader}>
            <Text style={styles.dirLabel}>Path:</Text>
            {logDir ? (
              <TouchableOpacity
                onPress={() => {
                  Clipboard.setString(logDir);
                  Alert.alert('Copied', 'Log directory path copied to clipboard');
                }}
                style={styles.copyButton}
              >
                <Text style={styles.copyButtonText}>Copy</Text>
              </TouchableOpacity>
            ) : null}
          </View>
          <Text style={styles.dirPath} selectable>
            {logDir || 'Loading...'}
          </Text>
        </View>
        <View style={styles.fileList}>
          <Text style={styles.dirLabel}>
            Files ({logFiles.length}):
          </Text>
          {logFiles.length === 0 ? (
            <Text style={styles.noFiles}>No log files yet</Text>
          ) : (
            logFiles.map((name, index) => (
              <Text key={index} style={styles.fileName} selectable>
                {name}
              </Text>
            ))
          )}
        </View>
      </View>

      {/* Write Logs */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Write Logs</Text>
        <TestInput
          placeholder="Custom log message"
          value={customMessage}
          onChangeText={setCustomMessage}
        />
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginTop: 10 }}>
          <TestButton
            title="Write Custom"
            onPress={writeCustomMessage}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140 }}
          />
          <TestButton
            title="Write All Levels"
            onPress={writeAllLevels}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140, backgroundColor: '#34C759' }}
          />
        </View>
        <TestButton
          title="Write 50 Batch Messages"
          onPress={writeBatchLogs}
          disabled={isLoading}
          style={{ marginTop: 10, backgroundColor: '#FF9500' }}
        />
      </View>

      {/* File Operations */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>File Operations</Text>
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
          <TestButton
            title="Refresh File List"
            onPress={getLogFilePaths}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140 }}
          />
          <TestButton
            title="Delete All Logs"
            onPress={deleteLogFiles}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140, backgroundColor: '#FF3B30' }}
          />
        </View>
      </View>

      <TestResult result={result} error={error} />

      {/* Documentation */}
      <View style={styles.docSection}>
        <Text style={styles.docText}>
          <Text style={{ fontWeight: '600' }}>NativeLogger API:</Text>{'\n'}
          {'• '}<Text style={{ fontWeight: '500' }}>write(level, msg):</Text> Write log (0=debug, 1=info, 2=warn, 3=error){'\n'}
          {'• '}<Text style={{ fontWeight: '500' }}>getLogFilePaths():</Text> Get all log file paths{'\n'}
          {'• '}<Text style={{ fontWeight: '500' }}>deleteLogFiles():</Text> Delete all log files{'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Log File Convention:</Text>{'\n'}
          {'• '}Active: app-latest.log{'\n'}
          {'• '}Archived: app-yyyy-MM-dd.i.log (max 6){'\n'}
          {'• '}Max file size: 20 MB, daily rolling{'\n'}
          {'• '}Directory: {'<CachesDir>'}/logs/
        </Text>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  section: {
    gap: 5,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 5,
  },
  dirBox: {
    backgroundColor: '#fff',
    padding: 12,
    borderRadius: 8,
  },
  dirHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 4,
  },
  dirLabel: {
    fontSize: 13,
    fontWeight: '600',
    color: '#333',
  },
  copyButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 4,
  },
  copyButtonText: {
    fontSize: 12,
    color: '#fff',
    fontWeight: '600',
  },
  dirPath: {
    fontSize: 12,
    fontFamily: 'Courier New',
    color: '#007AFF',
  },
  fileList: {
    backgroundColor: '#fff',
    padding: 12,
    borderRadius: 8,
    marginTop: 6,
  },
  fileName: {
    fontSize: 12,
    fontFamily: 'Courier New',
    color: '#666',
    marginBottom: 2,
  },
  noFiles: {
    fontSize: 13,
    color: '#999',
    fontStyle: 'italic',
  },
  docSection: {
    marginTop: 10,
    padding: 15,
    backgroundColor: '#fff',
    borderRadius: 8,
  },
  docText: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
  },
});
