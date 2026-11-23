import { useCallback, useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import {
  QrScanner,
  decodeQrImage,
  toQrScannerError,
} from '@onekeyfe/react-native-qr-scanner';
import type { ScanResult } from '@onekeyfe/react-native-qr-scanner';

import { TestPageBase } from './TestPageBase';

function describeResult(result: ScanResult): string {
  return result.kind === 'text'
    ? `text: ${result.text}`
    : `ur:${result.type} (${result.cbor.length} bytes)`;
}

export function QrScannerTestPage() {
  const [path, setPath] = useState('');
  const [imageResult, setImageResult] = useState('—');
  const [active, setActive] = useState(false);
  const [liveLog, setLiveLog] = useState<string[]>([]);

  const log = useCallback((line: string) => {
    setLiveLog((lines) => [line, ...lines].slice(0, 8));
  }, []);

  const decode = useCallback(async () => {
    setImageResult('decoding…');
    const started = Date.now();
    try {
      const text = await decodeQrImage(path.trim());
      setImageResult(
        `${text === null ? 'no QR found' : `found: ${text}`} (${Date.now() - started} ms)`
      );
    } catch (error) {
      const scannerError = toQrScannerError(error);
      setImageResult(`error ${scannerError.code}: ${scannerError.message}`);
    }
  }, [path]);

  return (
    <TestPageBase title="QR Scanner">
      <Text style={styles.heading}>decodeQrImage</Text>
      <TextInput
        testID="qr-image-path"
        style={styles.input}
        value={path}
        onChangeText={setPath}
        autoCapitalize="none"
        autoCorrect={false}
        placeholder="file:///… or /absolute/path.png"
      />
      <Pressable testID="qr-image-decode" style={styles.button} onPress={decode}>
        <Text style={styles.buttonText}>Decode image</Text>
      </Pressable>
      <Text testID="qr-image-result" selectable style={styles.result}>
        {imageResult}
      </Text>

      <Text style={styles.heading}>Live scanner</Text>
      <Pressable
        testID="qr-live-toggle"
        style={styles.button}
        onPress={() => setActive((value) => !value)}
      >
        <Text style={styles.buttonText}>{active ? 'Stop camera' : 'Start camera'}</Text>
      </Pressable>
      <View style={styles.preview}>
        <QrScanner
          style={StyleSheet.absoluteFill}
          active={active}
          mode="auto"
          onReady={() => log('ready')}
          onProgress={(progress) => log(`progress ${Math.round(progress * 100)}%`)}
          onResult={(result) => log(`result ${describeResult(result)}`)}
          onError={(error) =>
            log(`error ${'code' in error ? error.code : 'unknown'}: ${error.message}`)
          }
        />
      </View>
      <Text testID="qr-live-log" selectable style={styles.result}>
        {liveLog.join('\n') || '—'}
      </Text>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  heading: { fontSize: 16, fontWeight: '600', marginTop: 16, marginBottom: 8 },
  input: {
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#999',
    borderRadius: 8,
    padding: 8,
    fontSize: 12,
  },
  button: {
    marginTop: 8,
    backgroundColor: '#000',
    borderRadius: 8,
    paddingVertical: 10,
    alignItems: 'center',
  },
  buttonText: { color: '#fff', fontWeight: '600' },
  result: { marginTop: 8, fontSize: 12, fontFamily: 'Menlo' },
  preview: {
    marginTop: 8,
    height: 240,
    backgroundColor: '#111',
    borderRadius: 8,
    overflow: 'hidden',
  },
});
