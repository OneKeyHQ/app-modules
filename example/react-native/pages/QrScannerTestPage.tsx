import { useCallback, useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import {
  QrScanner,
  decodeQrImage,
  toQrScannerError,
} from '@onekeyfe/react-native-qr-scanner';
import type { ScanMode, ScanResult } from '@onekeyfe/react-native-qr-scanner';

import ImageCropPicker, {
  ImageCropPickerError,
} from '@onekeyfe/react-native-image-crop-picker';

import { TestPageBase } from './TestPageBase';

// FNV-1a 32-bit, matching the animated UR player page used for device checks.
function fnv1a(bytes: Uint8Array): string {
  let hash = 0x811c9dc5;
  for (const byte of bytes) {
    hash ^= byte;
    hash = Math.imul(hash, 0x01000193) >>> 0;
  }
  return hash.toString(16).padStart(8, '0');
}

function describeResult(result: ScanResult): string {
  return result.kind === 'text'
    ? `text: ${result.text}`
    : `ur:${result.type} (${result.cbor.length} bytes, fnv ${fnv1a(result.cbor)})`;
}

const MODES: ScanMode[] = ['auto', 'ur', 'static'];

export function QrScannerTestPage() {
  const [path, setPath] = useState('');
  const [imageResult, setImageResult] = useState('—');
  const [active, setActive] = useState(false);
  const [mode, setMode] = useState<ScanMode>('auto');
  const [sessionKey, setSessionKey] = useState(0);
  const [progress, setProgress] = useState(0);
  const [startedAt, setStartedAt] = useState<number | undefined>();
  const [liveLog, setLiveLog] = useState<string[]>([]);

  const log = useCallback((line: string) => {
    setLiveLog((lines) => [line, ...lines].slice(0, 8));
  }, []);

  const resetSession = useCallback(() => {
    setSessionKey((key) => key + 1);
    setProgress(0);
    setStartedAt(undefined);
    setLiveLog([]);
  }, []);

  const decodePath = useCallback(async (target: string) => {
    setImageResult('decoding…');
    const started = Date.now();
    try {
      const text = await decodeQrImage(target);
      setImageResult(
        `${text === null ? 'no QR found' : `found: ${text}`} (${Date.now() - started} ms)`
      );
    } catch (error) {
      const scannerError = toQrScannerError(error);
      setImageResult(`error ${scannerError.code}: ${scannerError.message}`);
    }
  }, []);

  const decode = useCallback(() => decodePath(path.trim()), [decodePath, path]);

  // The app's album flow: pick with image-crop-picker, decode the picked file.
  const pickAndDecode = useCallback(async () => {
    try {
      const image = await ImageCropPicker.openPicker({ preserveOriginal: true });
      setPath(image.path);
      await decodePath(image.path);
      await ImageCropPicker.cleanSingle(image.path).catch(() => {});
    } catch (error) {
      if (error instanceof ImageCropPickerError && error.code === 'E_PICKER_CANCELLED') {
        setImageResult('picker cancelled');
        return;
      }
      setImageResult(`picker error: ${String(error)}`);
    }
  }, [decodePath]);

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
      <Pressable testID="qr-image-pick" style={styles.button} onPress={pickAndDecode}>
        <Text style={styles.buttonText}>Pick from album and decode</Text>
      </Pressable>
      <Text testID="qr-image-result" selectable style={styles.result}>
        {imageResult}
      </Text>

      <Text style={styles.heading}>Live scanner</Text>
      <View style={styles.row}>
        {MODES.map((item) => (
          <Pressable
            key={item}
            testID={`qr-mode-${item}`}
            style={[styles.chip, item === mode && styles.chipActive]}
            onPress={() => {
              setMode(item);
              resetSession();
            }}
          >
            <Text style={item === mode ? styles.chipTextActive : undefined}>{item}</Text>
          </Pressable>
        ))}
        <Pressable testID="qr-session-reset" style={styles.chip} onPress={resetSession}>
          <Text>new session</Text>
        </Pressable>
      </View>
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
          mode={mode}
          sessionKey={sessionKey}
          onReady={() => log('ready')}
          onProgress={(value) => {
            setStartedAt((time) => time ?? Date.now());
            setProgress(value);
          }}
          onResult={(result) => {
            const elapsed = startedAt === undefined ? '' : ` in ${Date.now() - startedAt} ms`;
            log(`result ${describeResult(result)}${elapsed}`);
          }}
          onError={(error) =>
            log(`error ${'code' in error ? error.code : 'unknown'}: ${error.message}`)
          }
        />
      </View>
      <Text testID="qr-live-progress" style={styles.result}>
        progress {Math.round(progress * 100)}%
      </Text>
      <Text testID="qr-live-log" selectable style={styles.result}>
        {liveLog.join('\n') || '—'}
      </Text>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  heading: { fontSize: 16, fontWeight: '600', marginTop: 16, marginBottom: 8 },
  row: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  chip: {
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#999',
    borderRadius: 16,
    paddingHorizontal: 12,
    paddingVertical: 6,
  },
  chipActive: { backgroundColor: '#000', borderColor: '#000' },
  chipTextActive: { color: '#fff' },
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
