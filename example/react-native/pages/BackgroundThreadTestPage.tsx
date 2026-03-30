import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';

export function BackgroundThreadTestPage() {
  const [storeResult, setStoreResult] = useState<string>('');
  const [rpcLog, setRpcLog] = useState<string>('');
  const [ready, setReady] = useState(false);

  // Wait for sharedStore & sharedRPC to be available
  useEffect(() => {
    const timer = setInterval(() => {
      if (globalThis.sharedStore && globalThis.sharedRPC) {
        setReady(true);
        clearInterval(timer);
      }
    }, 100);
    return () => clearInterval(timer);
  }, []);

  // Register onWrite listener for RPC responses from background
  useEffect(() => {
    if (!ready) return;
    globalThis.sharedRPC!.onWrite((callId: string) => {
      if (!callId.endsWith(':result')) return;
      const raw = globalThis.sharedRPC?.read(callId);
      if (raw === undefined) return;
      const time = new Date().toLocaleTimeString();
      setRpcLog((prev) => `${prev}[${time}] ← ${raw}\n`);
    });
  }, [ready]);

  const appendLog = useCallback(
    (msg: string) => {
      const time = new Date().toLocaleTimeString();
      setRpcLog((prev) => `${prev}[${time}] → ${msg}\n`);
    },
    [],
  );

  // ── SharedStore tests ────────────────────────────────────────────────

  const handleStoreWrite = () => {
    if (!globalThis.sharedStore) return;
    globalThis.sharedStore.set('locale', 'zh-CN');
    globalThis.sharedStore.set('networkId', 42);
    globalThis.sharedStore.set('devMode', true);
    const keys = globalThis.sharedStore.keys();
    const values = keys
      .map((k: string) => `${k}=${globalThis.sharedStore?.get(k)}`)
      .join(', ');
    setStoreResult(`Wrote 3 values → ${values} (size=${globalThis.sharedStore.size})`);
  };

  // ── SharedRPC tests ──────────────────────────────────────────────────

  const sendRPC = (method: string, params: Record<string, unknown>) => {
    if (!globalThis.sharedRPC) return;
    const callId = `rpc_${Date.now()}`;
    const payload = JSON.stringify({ method, params });
    globalThis.sharedRPC.write(callId, payload);
    appendLog(`${method}(${JSON.stringify(params)})  id=${callId}`);
  };

  return (
    <TestPageBase title="Background Thread Test">
      {/* Status */}
      <Text style={styles.statusText}>
        SharedStore / SharedRPC: {ready ? 'Ready' : 'Waiting…'}
      </Text>

      {/* SharedStore */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>SharedStore</Text>
        <TestButton
          title="Set locale / networkId / devMode"
          onPress={handleStoreWrite}
          disabled={!ready}
        />
        <TestResult result={storeResult || null} />
      </View>

      {/* SharedRPC */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>SharedRPC (onWrite)</Text>
        <View style={styles.buttonRow}>
          <TestButton
            title="echo"
            onPress={() => sendRPC('echo', { msg: 'hello', ts: Date.now() })}
            disabled={!ready}
            style={styles.rowButton}
          />
          <TestButton
            title="add(3,7)"
            onPress={() => sendRPC('add', { a: 3, b: 7 })}
            disabled={!ready}
            style={styles.rowButton}
          />
          <TestButton
            title="unknown"
            onPress={() => sendRPC('foo', {})}
            disabled={!ready}
            style={[styles.rowButton, styles.warnButton]}
          />
        </View>
        <TestButton
          title="Clear Log"
          onPress={() => setRpcLog('')}
          style={styles.clearButton}
        />
        {rpcLog ? (
          <View style={styles.logContainer}>
            <Text style={styles.logText}>{rpcLog.trimEnd()}</Text>
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
    marginBottom: 4,
  },
  buttonRow: {
    flexDirection: 'row',
    gap: 8,
  },
  rowButton: {
    flex: 1,
  },
  warnButton: {
    backgroundColor: '#FF9500',
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
