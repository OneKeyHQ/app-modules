import React, { useState, useEffect, useRef } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import { BackgroundThread } from '@onekeyfe/react-native-background-thread';

BackgroundThread.initBackgroundThread();

export function BackgroundThreadTestPage() {
  const [pushResult, setPushResult] = useState<string>('');
  const [bridgeResult, setBridgeResult] = useState<string>('');
  const [bridgeAvailable, setBridgeAvailable] = useState(false);
  const drainTimer = useRef<ReturnType<typeof setInterval> | null>(null);

  // Check SharedBridge availability and start draining
  useEffect(() => {
    const check = setInterval(() => {
      if (globalThis.sharedBridge) {
        setBridgeAvailable(true);
        clearInterval(check);
      }
    }, 100);

    // Drain loop: pull messages from background via SharedBridge
    drainTimer.current = setInterval(() => {
      if (globalThis.sharedBridge?.hasMessages) {
        const messages = globalThis.sharedBridge.drain();
        messages.forEach((raw) => {
          try {
            const msg = JSON.parse(raw);
            setBridgeResult(
              (prev) =>
                `${prev}\n[${new Date().toLocaleTimeString()}] Received: ${JSON.stringify(msg)}`,
            );
          } catch {
            setBridgeResult((prev) => `${prev}\nParse error: ${raw}`);
          }
        });
      }
    }, 16);

    return () => {
      clearInterval(check);
      if (drainTimer.current) clearInterval(drainTimer.current);
    };
  }, []);

  // Push model: existing postBackgroundMessage / onBackgroundMessage
  const handlePushMessage = () => {
    const message = { type: 'test1' };
    BackgroundThread.onBackgroundMessage((event) => {
      setPushResult(`Received from background: ${event}`);
    });
    BackgroundThread.postBackgroundMessage(JSON.stringify(message));
    setPushResult(`Sent: ${JSON.stringify(message)}`);
  };

  // Pull model: SharedBridge send + drain
  const handleSharedBridgePing = () => {
    if (!globalThis.sharedBridge) {
      setBridgeResult('SharedBridge not available yet');
      return;
    }
    const msg = { type: 'ping', ts: Date.now() };
    globalThis.sharedBridge.send(JSON.stringify(msg));
    setBridgeResult(
      (prev) =>
        `${prev}\n[${new Date().toLocaleTimeString()}] Sent: ${JSON.stringify(msg)}`,
    );
  };

  const handleClearBridgeLog = () => {
    setBridgeResult('');
  };

  return (
    <TestPageBase title="Background Thread Test">
      {/* Push Model (existing) */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Push Model (postBackgroundMessage)</Text>
        <TestButton title="Send Message" onPress={handlePushMessage} />
        <TestResult result={pushResult || null} />
      </View>

      {/* Pull Model (SharedBridge) */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Pull Model (SharedBridge HostObject)</Text>
        <Text style={styles.statusText}>
          SharedBridge: {bridgeAvailable ? 'Available' : 'Waiting...'}
          {bridgeAvailable &&
            ` | isMain: ${globalThis.sharedBridge?.isMain}`}
        </Text>
        <TestButton
          title="Send Ping via SharedBridge"
          onPress={handleSharedBridgePing}
          disabled={!bridgeAvailable}
        />
        <TestButton
          title="Clear Log"
          onPress={handleClearBridgeLog}
          style={styles.clearButton}
        />
        {bridgeResult ? (
          <View style={styles.logContainer}>
            <Text style={styles.logText}>{bridgeResult.trim()}</Text>
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
