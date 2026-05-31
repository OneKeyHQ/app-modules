import { useCallback, useState } from 'react';
import { View, Text, StyleSheet, Platform } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeBundleCrypto } from '@onekeyfe/react-native-bundle-crypto';
import { getDemoWritableDir } from './demoPaths';
import { SAMPLE_SIGNED_MESSAGE } from './demoSamples';

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

export function BundleCryptoTestPage() {
  // --- sha256OfFile ---
  const [filePath, setFilePath] = useState('');
  const [sha256Result, setSha256Result] = useState<any>(null);
  const [sha256Error, setSha256Error] = useState<string | null>(null);

  // --- secureEqualHex ---
  const [hexA, setHexA] = useState(
    'bf3734ac6e59388fe23c40ce2960b6fd197c596af05dd08b3ccc8b201b78c52b',
  );
  const [hexB, setHexB] = useState(
    'bf3734ac6e59388fe23c40ce2960b6fd197c596af05dd08b3ccc8b201b78c52b',
  );
  const [eqResult, setEqResult] = useState<boolean | null>(null);

  // --- validateExtractedPathSafety ---
  const [safePath, setSafePath] = useState('');
  const [safeResult, setSafeResult] = useState<any>(null);
  const [safeError, setSafeError] = useState<string | null>(null);

  // --- verifyGpgCleartext ---
  const [signed, setSigned] = useState(SAMPLE_SIGNED_MESSAGE);
  const [gpgResult, setGpgResult] = useState<any>(null);
  const [gpgError, setGpgError] = useState<string | null>(null);

  // Prefill the file path with the demo download dir. Download a file via the
  // Range Downloader / OTA Pipeline page first, then point this at it to hash.
  const fillBundlePath = useCallback(() => {
    setSha256Error(null);
    setFilePath(`${getDemoWritableDir()}/`);
  }, []);

  const handleSha256 = useCallback(async () => {
    setSha256Result(null);
    setSha256Error(null);
    try {
      const res = await ReactNativeBundleCrypto.sha256OfFile(filePath);
      setSha256Result(res);
    } catch (err) {
      setSha256Error(err instanceof Error ? err.message : String(err));
    }
  }, [filePath]);

  const handleSecureEqual = useCallback(() => {
    // Synchronous constant-time hex comparison.
    setEqResult(ReactNativeBundleCrypto.secureEqualHex(hexA, hexB));
  }, [hexA, hexB]);

  const handleValidatePath = useCallback(async () => {
    setSafeResult(null);
    setSafeError(null);
    try {
      const ok =
        await ReactNativeBundleCrypto.validateExtractedPathSafety(safePath);
      setSafeResult({ safe: ok });
    } catch (err) {
      setSafeError(err instanceof Error ? err.message : String(err));
    }
  }, [safePath]);

  const handleVerifyGpg = useCallback(async () => {
    setGpgResult(null);
    setGpgError(null);
    try {
      const res = await ReactNativeBundleCrypto.verifyGpgCleartext(signed);
      setGpgResult(res);
    } catch (err) {
      setGpgError(err instanceof Error ? err.message : String(err));
    }
  }, [signed]);

  return (
    <TestPageBase title="Bundle Crypto">
      {/* sha256OfFile */}
      <View style={s.card}>
        <Text style={s.label}>sha256OfFile(path)</Text>
        <TestInput
          placeholder="/abs/path/file"
          value={filePath}
          onChangeText={setFilePath}
        />
        <TestButton title="Use live JS bundle path" onPress={fillBundlePath} />
        <TestButton
          title="Compute SHA-256"
          onPress={handleSha256}
          disabled={!filePath}
        />
        <TestResult result={sha256Result} error={sha256Error} />
      </View>

      {/* secureEqualHex */}
      <View style={s.card}>
        <Text style={s.label}>secureEqualHex(a, b)</Text>
        <TestInput placeholder="hex a" value={hexA} onChangeText={setHexA} />
        <TestInput placeholder="hex b" value={hexB} onChangeText={setHexB} />
        <TestButton title="Constant-time compare" onPress={handleSecureEqual} />
        {eqResult !== null && (
          <Text
            style={[
              s.boolText,
              { color: eqResult ? '#00d4aa' : '#ff453a' },
            ]}
          >
            {eqResult ? 'EQUAL (true)' : 'NOT EQUAL (false)'}
          </Text>
        )}
      </View>

      {/* validateExtractedPathSafety */}
      <View style={s.card}>
        <Text style={s.label}>validateExtractedPathSafety(dest)</Text>
        <TestInput
          placeholder="/abs/extract/dir"
          value={safePath}
          onChangeText={setSafePath}
        />
        <TestButton
          title="Check path safety"
          onPress={handleValidatePath}
          disabled={!safePath}
        />
        <TestResult result={safeResult} error={safeError} />
      </View>

      {/* verifyGpgCleartext */}
      <View style={s.card}>
        <Text style={s.label}>verifyGpgCleartext(signedMessage)</Text>
        <Text style={s.hint}>
          Runs the embedded-key GPG check. Sample blob below signs an
          appType:electron metadata; result shows valid + reason.
        </Text>
        <TestInput
          placeholder="-----BEGIN PGP SIGNED MESSAGE-----"
          value={signed}
          onChangeText={setSigned}
          multiline
        />
        <TestButton
          title="Verify GPG cleartext"
          onPress={handleVerifyGpg}
          disabled={!signed}
        />
        <TestResult result={gpgResult} error={gpgError} />
      </View>
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
  boolText: {
    fontSize: 16,
    fontWeight: '700',
  },
});
