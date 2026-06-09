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
import { getDemoWritableDir } from './demoPaths';
import { formatDownloadStats, fetchContentLength } from './demoFormat';

// End-to-end APK OTA pipeline — the Android app-update security flow. Unlike the
// JS-bundle OTA (cleartext-signed metadata + unzip), the APK flow uses a DETACHED
// SHA256SUMS.asc signature and ends at the system installer (no unzip):
//   range-downloader.download()        download the APK (8-range, apk channel)
//   bundle-crypto.sha256OfFile()       hash the APK
//   fetch(<apk>.SHA256SUMS.asc)        fetch the detached signature (manifest layer)
//   bundle-crypto.verifyDetachedAsc()  verify the signature, extract trusted sha256
//   bundle-crypto.secureEqualHex()     trusted sha256 == computed (real gate)
//   install                            Android system installer (out of demo scope)
//
// This is a REAL end-to-end verification: the detached ASC is a public sibling of
// the APK on the CDN, so the trusted hash actually matches the downloaded file.

const DEFAULT_URL =
  'https://web.onekey-asset.com/app-monorepo/v6.3.0/OneKey-Wallet-6.3.0-android.apk';

// The detached signature is a sibling of the APK: `<apk>.SHA256SUMS.asc`.
const ascUrlFor = (apkUrl: string) => `${apkUrl}.SHA256SUMS.asc`;

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

type StepKey =
  | 'download'
  | 'sha256'
  | 'fetchAsc'
  | 'verifyAsc'
  | 'shaMatch'
  | 'install';
type StepStatus = 'pending' | 'active' | 'done' | 'error';

const STEP_ORDER: StepKey[] = [
  'download',
  'sha256',
  'fetchAsc',
  'verifyAsc',
  'shaMatch',
  'install',
];

const STEP_LABEL: Record<StepKey, string> = {
  download: 'range-downloader.download() [apk]',
  sha256: 'bundle-crypto.sha256OfFile()',
  fetchAsc: 'fetch <apk>.SHA256SUMS.asc',
  verifyAsc: 'bundle-crypto.verifyDetachedAsc()',
  shaMatch: 'bundle-crypto.secureEqualHex()',
  install: 'install (platform handoff)',
};

const emptyStatus = (): Record<StepKey, StepStatus> => ({
  download: 'pending',
  sha256: 'pending',
  fetchAsc: 'pending',
  verifyAsc: 'pending',
  shaMatch: 'pending',
  install: 'pending',
});

const emptyDetail = (): Record<StepKey, string> => ({
  download: '',
  sha256: '',
  fetchAsc: '',
  verifyAsc: '',
  shaMatch: '',
  install: '',
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

export function ApkOtaPipelineTestPage() {
  const [url, setUrl] = useState(DEFAULT_URL);
  const [ascUrl, setAscUrl] = useState(ascUrlFor(DEFAULT_URL));
  const [taskId] = useState(() => `apk-ota-${Date.now()}`);
  const [destFilePath, setDestFilePath] = useState('');
  const [running, setRunning] = useState(false);

  const [steps, setSteps] = useState<Record<StepKey, StepStatus>>(emptyStatus);
  const [details, setDetails] = useState<Record<StepKey, string>>(emptyDetail);
  const [logs, setLogs] = useState<string[]>([]);
  const listenerIdRef = useRef<number | null>(null);

  useEffect(() => {
    setDestFilePath(`${getDemoWritableDir()}/${taskId}.apk`);
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
          event.channel === RangeDownloadChannel.Apk &&
          event.taskId === taskId
        ) {
          log(`[dl ${event.type}] ${event.progress}% ${event.message ?? ''}`);
        }
      },
    );
    listenerIdRef.current = lid;

    try {
      // 1) Concurrent download of the APK via range-downloader (apk channel), timed.
      setStep('download', 'active');
      const totalBytes = await fetchContentLength(url);
      const dlStart = Date.now();
      const dl = await ReactNativeRangeDownloader.download({
        channel: RangeDownloadChannel.Apk,
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

      // 2) Hash the APK via bundle-crypto.
      setStep('sha256', 'active');
      const hash = await ReactNativeBundleCrypto.sha256OfFile(dl.filePath);
      if (!hash.sha256) {
        setStep('sha256', 'error', `failureReason: ${hash.failureReason ?? '?'}`);
        return;
      }
      log(`sha256 ${hash.sha256}`);
      setStep('sha256', 'done', hash.sha256);

      // 3) Fetch the detached SHA256SUMS.asc signature (manifest layer, plain fetch).
      setStep('fetchAsc', 'active');
      const resp = await fetch(ascUrl);
      if (!resp.ok) {
        setStep('fetchAsc', 'error', `HTTP ${resp.status} for ${ascUrl}`);
        return;
      }
      const ascText = await resp.text();
      log(`asc fetched (${ascText.length} bytes)`);
      setStep('fetchAsc', 'done', `${ascText.length} bytes`);

      // 4) Verify the detached ASC signature; it yields the trusted sha256.
      setStep('verifyAsc', 'active');
      const asc = await ReactNativeBundleCrypto.verifyDetachedAsc(ascText);
      log(`verifyDetachedAsc valid=${asc.valid} sha256=${asc.sha256 ?? '-'} reason=${asc.reason ?? '-'}`);
      if (!asc.valid || !asc.sha256) {
        setStep('verifyAsc', 'error', `invalid signature: ${asc.reason ?? 'unknown'}`);
        return;
      }
      setStep('verifyAsc', 'done', `signature valid ✓ · trusted sha ${asc.sha256.slice(0, 16)}…`);

      // 5) Gate: trusted sha256 (from the signed ASC) == computed sha256.
      setStep('shaMatch', 'active');
      const matches = ReactNativeBundleCrypto.secureEqualHex(
        asc.sha256,
        hash.sha256,
      );
      log(`secureEqualHex(trusted, computed) = ${matches}`);
      if (!matches) {
        setStep('shaMatch', 'error', 'trusted sha ≠ downloaded APK (tampered!)');
        return;
      }
      setStep('shaMatch', 'done', 'trusted == computed ✓ (APK authentic)');

      // 6) Install — platform handoff. The verified APK would go to the Android
      // system package installer; app-update owns that integrated flow. Not wired
      // here (this demo downloads via range-downloader to its own path).
      setStep('install', 'active');
      setStep(
        'install',
        'done',
        Platform.OS === 'android'
          ? 'verified APK ready → Android system installer (see App Update)'
          : 'install is Android-only (iOS: N/A)',
      );
      log('PIPELINE OK ✓ (verified APK)');
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
  }, [ascUrl, destFilePath, setStep, taskId, url]);

  return (
    <TestPageBase title="APK OTA Pipeline">
      <View style={s.card}>
        <Text style={s.label}>APK URL</Text>
        <TestInput
          placeholder="https://….apk"
          value={url}
          onChangeText={(t) => {
            setUrl(t);
            setAscUrl(ascUrlFor(t));
          }}
        />
        <Text style={s.label}>DETACHED SIGNATURE (SHA256SUMS.asc)</Text>
        <TestInput
          placeholder="https://….apk.SHA256SUMS.asc"
          value={ascUrl}
          onChangeText={setAscUrl}
        />
        <Text style={s.meta}>dest: {destFilePath || '(resolving…)'}</Text>
      </View>

      <TestButton
        title={running ? 'Running pipeline…' : 'Run full APK OTA pipeline'}
        onPress={run}
        disabled={running || !url || !ascUrl || !destFilePath}
      />

      <View style={s.card}>
        <Text style={s.label}>PIPELINE (download → verify detached ASC → install)</Text>
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
