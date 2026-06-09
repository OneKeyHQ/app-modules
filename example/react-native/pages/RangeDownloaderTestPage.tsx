import { useCallback, useEffect, useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Platform,
} from 'react-native';
import { TestPageBase, TestButton, TestInput } from './TestPageBase';
import {
  ReactNativeRangeDownloader,
  RangeDownloadChannel,
} from '@onekeyfe/react-native-range-downloader';
import type {
  RangeDownloadEvent,
  RangeDownloadResult,
} from '@onekeyfe/react-native-range-downloader';
import { getDemoWritableDir } from './demoPaths';

// A real public HTTPS asset that supports HTTP range requests (same OneKey
// asset CDN used by the bundle-update demo). The range downloader will fan
// this out into `segmentCount` concurrent byte ranges.
const DEFAULT_URL =
  'https://uni.onekey-asset.com/dashboard/version-update/12652642/ios-bundle.zip';

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

// Place the destination file under the demo's writable directory.
async function deriveDestFilePath(fileName: string): Promise<string> {
  return `${getDemoWritableDir()}/${fileName}`;
}

interface LogLine {
  ts: number;
  text: string;
}

export function RangeDownloaderTestPage() {
  const [url, setUrl] = useState(DEFAULT_URL);
  const [destFilePath, setDestFilePath] = useState('');
  const [taskId] = useState(() => `chart-demo-${Date.now()}`);
  const [running, setRunning] = useState(false);
  const [outcome, setOutcome] = useState<RangeDownloadResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [logs, setLogs] = useState<LogLine[]>([]);
  const listenerIdRef = useRef<number | null>(null);

  const appendLog = useCallback((text: string) => {
    setLogs((prev) => [...prev.slice(-60), { ts: Date.now(), text }]);
  }, []);

  // Resolve a default destination path once on mount.
  useEffect(() => {
    let mounted = true;
    void deriveDestFilePath(`${taskId}.zip`).then((p) => {
      if (mounted) setDestFilePath(p);
    });
    return () => {
      mounted = false;
    };
  }, [taskId]);

  // Always clean up the shared listener registry on unmount.
  useEffect(() => {
    return () => {
      if (listenerIdRef.current !== null) {
        ReactNativeRangeDownloader.removeDownloadListener(listenerIdRef.current);
        listenerIdRef.current = null;
      }
    };
  }, []);

  const handleDownload = useCallback(async () => {
    setRunning(true);
    setOutcome(null);
    setError(null);
    setLogs([]);

    // Register listener; we filter by channel + taskId since all consumers
    // share one registry.
    const lid = ReactNativeRangeDownloader.addDownloadListener(
      (event: RangeDownloadEvent) => {
        if (
          event.channel !== RangeDownloadChannel.Chart ||
          event.taskId !== taskId
        ) {
          return;
        }
        appendLog(
          `[${event.type}] ${event.progress}% ${event.message ?? ''}`.trim(),
        );
      },
    );
    listenerIdRef.current = lid;

    try {
      const result = await ReactNativeRangeDownloader.download({
        channel: RangeDownloadChannel.Chart,
        taskId,
        url,
        destFilePath,
        // Optional knobs left at native defaults (segmentCount=8, 2MB threshold).
      });
      setOutcome(result);
      appendLog(
        `resolved outcome=${result.outcome}` +
          (result.fallbackReason ? ` reason=${result.fallbackReason}` : ''),
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    } finally {
      if (listenerIdRef.current !== null) {
        ReactNativeRangeDownloader.removeDownloadListener(listenerIdRef.current);
        listenerIdRef.current = null;
      }
      setRunning(false);
    }
  }, [appendLog, destFilePath, taskId, url]);

  const handleDiscard = useCallback(async () => {
    setError(null);
    try {
      await ReactNativeRangeDownloader.discardArtifacts(
        RangeDownloadChannel.Chart,
        taskId,
        destFilePath,
      );
      appendLog('discardArtifacts() done — .segN/.partial/.progress removed');
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }, [appendLog, destFilePath, taskId]);

  return (
    <TestPageBase title="Range Downloader">
      <View style={s.card}>
        <Text style={s.label}>SOURCE URL (HTTPS, range-capable)</Text>
        <TestInput placeholder="https://..." value={url} onChangeText={setUrl} />
      </View>

      <View style={s.card}>
        <Text style={s.label}>DEST FILE PATH</Text>
        <TestInput
          placeholder="/abs/path/file.zip"
          value={destFilePath}
          onChangeText={setDestFilePath}
        />
        <Text style={s.meta}>channel: chart</Text>
        <Text style={s.meta}>taskId: {taskId}</Text>
      </View>

      <TestButton
        title={running ? 'Downloading…' : 'Concurrent download()'}
        onPress={handleDownload}
        disabled={running || !url || !destFilePath}
      />
      <TestButton
        title="discardArtifacts()"
        onPress={handleDiscard}
        disabled={running || !destFilePath}
      />

      {outcome && (
        <View style={s.card}>
          <Text style={s.label}>OUTCOME</Text>
          <Text
            style={[
              s.outcomeText,
              { color: outcome.outcome === 'completed' ? '#00d4aa' : '#ff9f0a' },
            ]}
          >
            {outcome.outcome}
          </Text>
          <Text style={s.meta}>filePath: {outcome.filePath}</Text>
          {outcome.fallbackReason ? (
            <Text style={s.meta}>fallbackReason: {outcome.fallbackReason}</Text>
          ) : null}
        </View>
      )}

      {error ? (
        <View style={s.card}>
          <Text style={s.label}>ERROR</Text>
          <Text style={s.errorText}>{error}</Text>
        </View>
      ) : null}

      <View style={s.card}>
        <Text style={s.label}>EVENT STREAM</Text>
        <ScrollView style={s.logBox} nestedScrollEnabled>
          {logs.length === 0 ? (
            <Text style={s.logEmpty}>No events yet.</Text>
          ) : (
            logs.map((l, i) => (
              <Text key={`${l.ts}-${i}`} style={s.logLine}>
                {l.text}
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
  },
  meta: {
    fontSize: 11,
    color: '#8e8e93',
    fontFamily: mono,
    marginTop: 6,
  },
  outcomeText: {
    fontSize: 18,
    fontWeight: '700',
  },
  errorText: {
    fontSize: 13,
    color: '#ff453a',
    fontFamily: mono,
  },
  logBox: {
    maxHeight: 220,
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
