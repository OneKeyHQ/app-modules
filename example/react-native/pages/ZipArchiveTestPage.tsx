import { useCallback, useState } from 'react';
import { View, Text, StyleSheet, Platform } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
// Production consumers import these from `react-native-zip-archive` (an npm
// alias mapped to @onekeyfe/react-native-zip-archive). The example imports the
// real workspace package directly to keep the green-field demo self-contained.
import {
  unzip,
  getUncompressedSize,
  isPasswordProtected,
} from '@onekeyfe/react-native-zip-archive';
import { getDemoWritableDir } from './demoPaths';

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

export function ZipArchiveTestPage() {
  const [zipPath, setZipPath] = useState('');
  const [destDir, setDestDir] = useState('');
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  // Suggest a writable destination directory so unzip has a target out of the box.
  const fillDestDir = useCallback(() => {
    setError(null);
    setDestDir(`${getDemoWritableDir()}/zip-demo-out`);
  }, []);

  const handleInspect = useCallback(async () => {
    setBusy(true);
    setResult(null);
    setError(null);
    try {
      const [size, locked] = await Promise.all([
        getUncompressedSize(zipPath),
        isPasswordProtected(zipPath),
      ]);
      setResult({ uncompressedSize: size, passwordProtected: locked });
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    } finally {
      setBusy(false);
    }
  }, [zipPath]);

  const handleUnzip = useCallback(async () => {
    setBusy(true);
    setResult(null);
    setError(null);
    try {
      const size = await getUncompressedSize(zipPath);
      const out = await unzip(zipPath, destDir);
      setResult({ unzippedTo: out, uncompressedSize: size });
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    } finally {
      setBusy(false);
    }
  }, [zipPath, destDir]);

  return (
    <TestPageBase title="Zip Archive">
      <View style={s.card}>
        <Text style={s.label}>ZIP FILE PATH</Text>
        <TestInput
          placeholder="/abs/path/archive.zip"
          value={zipPath}
          onChangeText={setZipPath}
        />
        <Text style={s.hint}>
          Use a real .zip on disk — e.g. a bundle downloaded via the Range
          Downloader demo.
        </Text>
      </View>

      <View style={s.card}>
        <Text style={s.label}>DESTINATION DIR</Text>
        <TestInput
          placeholder="/abs/extract/dir"
          value={destDir}
          onChangeText={setDestDir}
        />
        <TestButton title="Suggest dest dir" onPress={fillDestDir} />
      </View>

      <TestButton
        title="Inspect (getUncompressedSize + isPasswordProtected)"
        onPress={handleInspect}
        disabled={busy || !zipPath}
      />
      <TestButton
        title={busy ? 'Working…' : 'unzip(from, to)'}
        onPress={handleUnzip}
        disabled={busy || !zipPath || !destDir}
      />

      <TestResult result={result} error={error} />
    </TestPageBase>
  );
}

const s = StyleSheet.create({
  card: {
    backgroundColor: '#1c1c1e',
    borderRadius: 12,
    padding: 16,
    borderWidth: 1,
    borderColor: '#38383a',
    gap: 10,
  },
  label: {
    fontSize: 11,
    fontWeight: '700',
    color: '#8e8e93',
    letterSpacing: 1.2,
    fontFamily: mono,
  },
  hint: {
    fontSize: 11,
    color: '#636366',
    lineHeight: 16,
  },
});
