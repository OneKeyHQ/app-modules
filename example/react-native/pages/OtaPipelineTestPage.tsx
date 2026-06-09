import { useCallback, useEffect, useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Platform,
  ActivityIndicator,
} from 'react-native';
import { TestPageBase, TestButton, TestInput } from './TestPageBase';
import {
  ReactNativeRangeDownloader,
  RangeDownloadChannel,
} from '@onekeyfe/react-native-range-downloader';
import type { RangeDownloadEvent } from '@onekeyfe/react-native-range-downloader';
import { ReactNativeBundleCrypto } from '@onekeyfe/react-native-bundle-crypto';
import { unzip } from '@onekeyfe/react-native-zip-archive';
import { getDemoWritableDir } from './demoPaths';
import { SAMPLE_SIGNED_MESSAGE, EXPECTED_BUNDLE_SHA256 } from './demoSamples';
import { formatDownloadStats, fetchContentLength } from './demoFormat';

// End-to-end chart OTA pipeline — the real security flow, wiring all three new
// capabilities together:
//   range-downloader.download()          download the artifact (8-range)
//   bundle-crypto.sha256OfFile()         hash the artifact
//   bundle-crypto.secureEqualHex()       verify hash == expected (tamper check)
//   bundle-crypto.verifyGpgCleartext()   verify the GPG signature
//   zip-archive.unzip()                  extract the artifact
//   bundle-crypto.validateExtractedPathSafety()  guard against zip-slip
// This proves the modules interoperate over a real OTA artifact AND that the
// verification primitives actually gate the pipeline.

const DEFAULT_URL =
  'https://uni.onekey-asset.com/dashboard/version-update/12652642/ios-bundle.zip';

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

type StepKey =
  | 'download'
  | 'sha256'
  | 'shaMatch'
  | 'gpg'
  | 'unzip'
  | 'pathSafe';
type StepStatus = 'pending' | 'active' | 'done' | 'error';

const STEP_ORDER: StepKey[] = [
  'download',
  'sha256',
  'shaMatch',
  'gpg',
  'unzip',
  'pathSafe',
];

const STEP_LABEL: Record<StepKey, string> = {
  download: 'range-downloader.download()',
  sha256: 'bundle-crypto.sha256OfFile()',
  shaMatch: 'bundle-crypto.secureEqualHex()',
  gpg: 'bundle-crypto.verifyGpgCleartext()',
  unzip: 'zip-archive.unzip()',
  pathSafe: 'bundle-crypto.validateExtractedPathSafety()',
};

function dirOf(path: string): string {
  const idx = path.lastIndexOf('/');
  return idx > 0 ? path.slice(0, idx) : path;
}

const emptyStatus = (): Record<StepKey, StepStatus> => ({
  download: 'pending',
  sha256: 'pending',
  shaMatch: 'pending',
  gpg: 'pending',
  unzip: 'pending',
  pathSafe: 'pending',
});

const emptyDetail = (): Record<StepKey, string> => ({
  download: '',
  sha256: '',
  shaMatch: '',
  gpg: '',
  unzip: '',
  pathSafe: '',
});

function StepLine({
  n,
  label,
  status,
  detail,
}: {
  n: number;
  label: string;
  status: StepStatus;
  detail?: string;
}) {
  const color =
    status === 'done'
      ? '#00d4aa'
      : status === 'error'
      ? '#ff453a'
      : status === 'active'
      ? '#0a84ff'
      : '#636366';
  return (
    <View style={s.stepLine}>
      <View style={[s.stepDot, { borderColor: color }]}>
        {status === 'active' ? (
          <ActivityIndicator size="small" color={color} />
        ) : (
          <Text style={[s.stepDotText, { color }]}>
            {status === 'done' ? '✓' : status === 'error' ? '!' : n}
          </Text>
        )}
      </View>
      <View style={s.stepBody}>
        <Text style={[s.stepLabel, { color }]}>{label}</Text>
        {detail ? <Text style={s.stepDetail}>{detail}</Text> : null}
      </View>
    </View>
  );
}

export function OtaPipelineTestPage() {
  const [url, setUrl] = useState(DEFAULT_URL);
  const [expectedSha, setExpectedSha] = useState(EXPECTED_BUNDLE_SHA256);
  const [taskId] = useState(() => `ota-pipeline-${Date.now()}`);
  const [destFilePath, setDestFilePath] = useState('');
  const [running, setRunning] = useState(false);

  const [steps, setSteps] = useState<Record<StepKey, StepStatus>>(emptyStatus);
  const [details, setDetails] = useState<Record<StepKey, string>>(emptyDetail);
  const [logs, setLogs] = useState<string[]>([]);
  const listenerIdRef = useRef<number | null>(null);

  useEffect(() => {
    setDestFilePath(`${getDemoWritableDir()}/${taskId}.zip`);
  }, [taskId]);

  useEffect(() => {
    return () => {
      if (listenerIdRef.current !== null) {
        ReactNativeRangeDownloader.removeDownloadListener(listenerIdRef.current);
        listenerIdRef.current = null;
      }
    };
  }, []);

  const setStep = useCallback(
    (key: StepKey, status: StepStatus, detail?: string) => {
      setSteps((prev) => ({ ...prev, [key]: status }));
      if (detail !== undefined) {
        setDetails((prev) => ({ ...prev, [key]: detail }));
      }
    },
    [],
  );

  const run = useCallback(async () => {
    setRunning(true);
    setLogs([]);
    setSteps(emptyStatus());
    setDetails(emptyDetail());
    const log = (t: string) => setLogs((prev) => [...prev.slice(-80), t]);

    const lid = ReactNativeRangeDownloader.addDownloadListener(
      (event: RangeDownloadEvent) => {
        if (
          event.channel === RangeDownloadChannel.Chart &&
          event.taskId === taskId
        ) {
          log(`[dl ${event.type}] ${event.progress}% ${event.message ?? ''}`);
        }
      },
    );
    listenerIdRef.current = lid;

    try {
      // 1) Concurrent download via range-downloader (timed for the speed readout).
      setStep('download', 'active');
      const totalBytes = await fetchContentLength(url);
      const dlStart = Date.now();
      const dl = await ReactNativeRangeDownloader.download({
        channel: RangeDownloadChannel.Chart,
        taskId,
        url,
        destFilePath,
      });
      const dlElapsedMs = Date.now() - dlStart;
      log(`download outcome=${dl.outcome}`);
      if (dl.outcome === 'fallback') {
        setStep(
          'download',
          'error',
          `fallback: ${dl.fallbackReason ?? 'concurrent unavailable'}`,
        );
        return;
      }
      const dlStats = formatDownloadStats(totalBytes, dlElapsedMs);
      log(`download ${dlStats}`);
      setStep('download', 'done', `${dlStats}\nfile: ${dl.filePath}`);

      // 2) Hash the artifact via bundle-crypto.
      setStep('sha256', 'active');
      const hash = await ReactNativeBundleCrypto.sha256OfFile(dl.filePath);
      if (!hash.sha256) {
        setStep('sha256', 'error', `failureReason: ${hash.failureReason ?? '?'}`);
        return;
      }
      log(`sha256 ${hash.sha256}`);
      setStep('sha256', 'done', hash.sha256);

      // 3) Verify hash == expected (constant-time). Tamper/corruption gate.
      setStep('shaMatch', 'active');
      const matches = ReactNativeBundleCrypto.secureEqualHex(
        hash.sha256,
        expectedSha.trim(),
      );
      log(`secureEqualHex(computed, expected) = ${matches}`);
      if (!matches) {
        setStep(
          'shaMatch',
          'error',
          `computed ≠ expected (${expectedSha.trim().slice(0, 16)}…)`,
        );
        return;
      }
      setStep('shaMatch', 'done', 'computed == expected ✓');

      // 4) Verify the GPG signature of the signed manifest.
      setStep('gpg', 'active');
      const gpg = await ReactNativeBundleCrypto.verifyGpgCleartext(
        SAMPLE_SIGNED_MESSAGE,
      );
      log(`gpg valid=${gpg.valid} sha256=${gpg.sha256 ?? '-'} reason=${gpg.reason ?? '-'}`);
      if (!gpg.valid) {
        setStep('gpg', 'error', `invalid signature: ${gpg.reason ?? 'unknown'}`);
        return;
      }
      // Signature is valid. The sample manifest signs a different artifact, so
      // its payload sha256 won't equal this bundle's hash (real OTA: it would).
      const sigMatches = gpg.sha256
        ? ReactNativeBundleCrypto.secureEqualHex(gpg.sha256, hash.sha256)
        : false;
      setStep(
        'gpg',
        'done',
        sigMatches
          ? `signature valid ✓ · sha matches bundle ✓`
          : `signature valid ✓ · payload sha ${(gpg.sha256 ?? '').slice(0, 12)}… (sample → other bundle)`,
      );

      // 5) Unzip the artifact via zip-archive.
      setStep('unzip', 'active');
      const outDir = `${dirOf(dl.filePath)}/${taskId}-extracted`;
      const out = await unzip(dl.filePath, outDir);
      log(`unzipped to ${out}`);
      setStep('unzip', 'done', out);

      // 6) Guard the extracted tree against symlink / path-traversal (zip-slip).
      setStep('pathSafe', 'active');
      const safe = await ReactNativeBundleCrypto.validateExtractedPathSafety(
        outDir,
      );
      log(`validateExtractedPathSafety = ${safe}`);
      if (!safe) {
        setStep('pathSafe', 'error', 'unsafe extracted path (symlink/traversal)');
        return;
      }
      setStep('pathSafe', 'done', 'extracted tree is safe ✓');
      log('PIPELINE OK ✓');
    } catch (err) {
      const msg = err instanceof Error ? err.message : String(err);
      log(`ERROR ${msg}`);
      setSteps((prev) => {
        const next = { ...prev };
        for (const k of STEP_ORDER) {
          if (next[k] === 'active' || next[k] === 'pending') {
            next[k] = 'error';
            break;
          }
        }
        return next;
      });
    } finally {
      if (listenerIdRef.current !== null) {
        ReactNativeRangeDownloader.removeDownloadListener(listenerIdRef.current);
        listenerIdRef.current = null;
      }
      setRunning(false);
    }
  }, [destFilePath, expectedSha, setStep, taskId, url]);

  return (
    <TestPageBase title="OTA Pipeline">
      <View style={s.card}>
        <Text style={s.label}>CHART OTA ARTIFACT URL</Text>
        <TestInput placeholder="https://..." value={url} onChangeText={setUrl} />
        <Text style={s.label}>EXPECTED SHA256 (corrupt to see the gate fail)</Text>
        <TestInput
          placeholder="64 hex"
          value={expectedSha}
          onChangeText={setExpectedSha}
        />
        <Text style={s.meta}>dest: {destFilePath || '(resolving…)'}</Text>
      </View>

      <TestButton
        title={running ? 'Running pipeline…' : 'Run full OTA pipeline'}
        onPress={run}
        disabled={running || !url || !destFilePath}
      />

      <View style={s.card}>
        <Text style={s.label}>PIPELINE (download → verify → unzip → guard)</Text>
        {STEP_ORDER.map((k, i) => (
          <StepLine
            key={k}
            n={i + 1}
            label={STEP_LABEL[k]}
            status={steps[k]}
            detail={details[k]}
          />
        ))}
      </View>

      <View style={s.card}>
        <Text style={s.label}>LOG</Text>
        <ScrollView style={s.logBox} nestedScrollEnabled>
          {logs.length === 0 ? (
            <Text style={s.logEmpty}>Idle.</Text>
          ) : (
            logs.map((l, i) => (
              <Text key={i} style={s.logLine}>
                {l}
              </Text>
            ))
          )}
        </ScrollView>
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
  },
  label: {
    fontSize: 11,
    fontWeight: '700',
    color: '#8e8e93',
    letterSpacing: 1.2,
    marginBottom: 10,
    marginTop: 4,
  },
  meta: {
    fontSize: 11,
    color: '#8e8e93',
    fontFamily: mono,
    marginTop: 8,
  },
  stepLine: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  stepDot: {
    width: 30,
    height: 30,
    borderRadius: 15,
    borderWidth: 2,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 12,
  },
  stepDotText: {
    fontSize: 14,
    fontWeight: '700',
  },
  stepBody: {
    flex: 1,
  },
  stepLabel: {
    fontSize: 13,
    fontWeight: '600',
    fontFamily: mono,
  },
  stepDetail: {
    fontSize: 10,
    color: '#8e8e93',
    fontFamily: mono,
    marginTop: 2,
  },
  logBox: {
    maxHeight: 200,
    backgroundColor: '#0a0a0a',
    borderRadius: 8,
    padding: 10,
  },
  logEmpty: {
    fontSize: 12,
    color: '#636366',
    fontFamily: mono,
  },
  logLine: {
    fontSize: 11,
    color: '#d1d1d6',
    fontFamily: mono,
    marginBottom: 3,
  },
});
