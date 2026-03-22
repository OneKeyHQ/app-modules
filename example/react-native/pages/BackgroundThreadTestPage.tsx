import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import { BackgroundThread } from '@onekeyfe/react-native-background-thread';

BackgroundThread.initBackgroundThread();

export function BackgroundThreadTestPage() {
  const [pushResult, setPushResult] = useState<string>('');
  const [storeResult, setStoreResult] = useState<string>('');
  const [rpcResult, setRpcResult] = useState<string>('');
  const [storeAvailable, setStoreAvailable] = useState(false);

  useEffect(() => {
    const check = setInterval(() => {
      if (globalThis.sharedStore && globalThis.sharedRPC) {
        setStoreAvailable(true);
        clearInterval(check);
      }
    }, 100);
    return () => clearInterval(check);
  }, []);

  // Listen for RPC responses from background
  useEffect(() => {
    const sub = BackgroundThread.onBackgroundMessage((msg) => {
      try {
        const parsed = JSON.parse(msg);
        if (parsed.type === 'rpc_response' && parsed.callId) {
          const result = globalThis.sharedRPC?.read(parsed.callId);
          setRpcResult(
            (prev) =>
              `${prev}\n[${new Date().toLocaleTimeString()}] Response: ${result}`,
          );
        } else {
          setPushResult(`Received: ${msg}`);
        }
      } catch {
        setPushResult(`Received: ${msg}`);
      }
    });
    return sub;
  }, []);

  // Push model
  const handlePushMessage = () => {
    const message = { type: 'test1' };
    BackgroundThread.postBackgroundMessage(JSON.stringify(message));
    setPushResult(`Sent: ${JSON.stringify(message)}`);
  };

  // SharedStore test
  const handleStoreTest = () => {
    if (!globalThis.sharedStore) {
      setStoreResult('SharedStore not available');
      return;
    }
    globalThis.sharedStore.set('locale', 'zh-CN');
    globalThis.sharedStore.set('networkId', 42);
    globalThis.sharedStore.set('devMode', true);
    const keys = globalThis.sharedStore.keys();
    const values = keys
      .map((k) => `${k}=${globalThis.sharedStore?.get(k)}`)
      .join(', ');
    setStoreResult(`Set 3 values. Current: ${values} | size=${globalThis.sharedStore.size}`);
  };

  // SharedRPC test
  const handleRpcTest = () => {
    if (!globalThis.sharedRPC) {
      setRpcResult('SharedRPC not available');
      return;
    }
    const callId = `rpc_${Date.now()}`;
    globalThis.sharedRPC.write(
      callId,
      JSON.stringify({ method: 'echo', params: { ts: Date.now() } }),
    );
    BackgroundThread.postBackgroundMessage(
      JSON.stringify({ type: 'rpc', callId }),
    );
    setRpcResult(
      (prev) =>
        `${prev}\n[${new Date().toLocaleTimeString()}] Sent RPC: ${callId}`,
    );
  };

  return (
    <TestPageBase title="Background Thread Test">
      {/* Push Model */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Push Model (postBackgroundMessage)</Text>
        <TestButton title="Send Message" onPress={handlePushMessage} />
        <TestResult result={pushResult || null} />
      </View>

      {/* SharedStore */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>SharedStore (persistent key-value)</Text>
        <Text style={styles.statusText}>
          Available: {storeAvailable ? 'Yes' : 'Waiting...'}
        </Text>
        <TestButton
          title="Set & Read Values"
          onPress={handleStoreTest}
          disabled={!storeAvailable}
        />
        <TestResult result={storeResult || null} />
      </View>

      {/* SharedRPC */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>SharedRPC (temporary read-and-delete)</Text>
        <TestButton
          title="Send RPC Call"
          onPress={handleRpcTest}
          disabled={!storeAvailable}
        />
        <TestButton
          title="Clear Log"
          onPress={() => setRpcResult('')}
          style={styles.clearButton}
        />
        {rpcResult ? (
          <View style={styles.logContainer}>
            <Text style={styles.logText}>{rpcResult.trim()}</Text>
          </View>
        ) : null}
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  section: {
    marginBottom: 20,
    gap: 10,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#333',
  },
  statusText: {
    fontSize: 13,
    color: '#666',
    fontFamily: 'Courier New',
  },
  clearButton: {
    backgroundColor: '#8E8E93',
  },
  logContainer: {
    backgroundColor: '#1a1a2e',
    padding: 12,
    borderRadius: 8,
    maxHeight: 300,
  },
  logText: {
    fontSize: 12,
    color: '#00ff88',
    fontFamily: 'Courier New',
    lineHeight: 18,
  },
});
