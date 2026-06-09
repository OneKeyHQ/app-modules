import { useCallback, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, Platform } from 'react-native';
import { callback } from 'react-native-nitro-modules';
import { TestButton } from './TestPageBase';
import {
  ChartWebviewView,
  type ChartWebviewMethods,
} from '@onekeyfe/react-native-chart-webview';

// MVP goal: prove the chart loads OFFLINE (no white screen -> virtual same-origin
// + localStorage work) and that the message bridge round-trips both ways. The
// business protocol (real kline) lives in JS; here a stub answers kline requests
// with fake bars so the chart can draw something.

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

const PARAMS = {
  symbol: 'BTC',
  type: 'market',
  theme: 'dark',
  locale: 'zh-CN',
  // Tell the chart it runs on a native platform (not 'web') so its outbound
  // bridge uses `window.$onekey.$private.request(...)` — the channel our native
  // host injects — instead of the web `window.parent.postMessage` path.
  platform: Platform.OS === 'ios' ? 'ios' : 'android',
  appVersion: '6.4.0',
  decimal: '8',
  networkId: 'btc--0',
  address: '',
  // Hyperliquid scene: the chart fetches candles DIRECTLY (over the network)
  // instead of via the app bridge, so real candles render without a business
  // kline handler. This is the mode the public chart URL uses. The bridge is
  // still exercised for non-candle traffic (symbol sync, layout, marks).
  scene: 'market-hyperliquid',
  storageNamespace: 'market-hyperliquid',
};

// Map a TradingView resolution string to seconds.
function resolutionToSeconds(res: string): number {
  if (!res) return 3600;
  if (res === 'D' || res === '1D') return 86400;
  if (res === 'W' || res === '1W') return 604800;
  if (res === 'M' || res === '1M') return 2592000;
  const n = parseInt(res, 10);
  return Number.isFinite(n) && n > 0 ? n * 60 : 3600; // bare number = minutes
}

// Build fake candles in the OneKey market kline shape:
//   { points: [{ o, h, l, c, v, t }], total }
// where t is a UNIX timestamp in SECONDS. Spans [from, to] at the resolution
// interval so the chart's requested window is filled.
function buildKline(fromSec: number, toSec: number, resolution: string) {
  const step = resolutionToSeconds(resolution);
  const now = Math.floor(Date.now() / 1000);
  const end = toSec && toSec > 0 ? Math.min(toSec, now) : now;
  // Cap to a reasonable page (a real datafeed paginates); generating the full
  // [from, to] window can be tens of thousands of bars, which the chart rejects.
  const MAX_BARS = 300;
  const fromCandidate = fromSec && fromSec > 0 ? fromSec : end - step * MAX_BARS;
  const start = Math.max(fromCandidate, end - step * MAX_BARS);
  const points = [];
  let price = 60000;
  for (let t = start; t <= end; t += step) {
    const open = price;
    const drift = Math.sin(t / step / 6) * 400;
    const close = open + drift + (t % 2 === 0 ? 120 : -90);
    const high = Math.max(open, close) + 180;
    const low = Math.min(open, close) - 180;
    points.push({
      o: open,
      h: high,
      l: low,
      c: close,
      v: 1000 + (t % 500),
      t,
    });
    price = close;
  }
  return { points, total: points.length };
}

// Nitro exposes a view's imperative methods through the `hybridRef` PROP (not the
// React `ref`): the wrapped callback receives a ref object whose `.current` is the
// live HybridObject. We hold that ref object and read `.current` fresh on each call.
type ChartHybridRef = { current: ChartWebviewMethods | null };

export function ChartWebViewTestPage() {
  const hybridRefHolder = useRef<ChartHybridRef | null>(null);
  const chart = useCallback(
    () => hybridRefHolder.current?.current ?? null,
    [],
  );
  const [remote, setRemote] = useState(false);
  const [logs, setLogs] = useState<string[]>([]);
  const log = useCallback(
    (t: string) => setLogs((p) => [...p.slice(-60), t]),
    [],
  );

  const onMessage = useCallback(
    (raw: string) => {
      log(`◀ ${raw.slice(0, 100)}`);
      // Best-effort stub mirroring the real TradingViewV2 handler so the chart
      // actually draws. The chart sends a `$private` envelope whose inner `data`
      // is `{ method:'tradingview_getHistoryData', resolution, from, to, ... }`;
      // we reply `{ type:'kLineData', payload:{ type:'history', kLineData, requestData } }`
      // where kLineData is `{ points:[{o,h,l,c,v,t}], total }` and requestData
      // echoes that inner object so the chart can correlate the response.
      if (!(raw.includes('getKLineData') || raw.includes('getHistoryData'))) {
        return;
      }
      let inner: Record<string, unknown> = {};
      try {
        const msg = JSON.parse(raw) as { data?: Record<string, unknown> };
        inner = (msg.data as Record<string, unknown>) ?? {};
      } catch {
        inner = {};
      }
      const resolution = String(inner.resolution ?? '60');
      const from = Number(inner.from ?? 0);
      const to = Number(inner.to ?? 0);
      const kLineData = buildKline(from, to, resolution);
      const reply = JSON.stringify({
        type: 'kLineData',
        payload: { type: 'history', kLineData, requestData: inner },
      });
      chart()?.postMessage(reply);
      log(`▶ kLineData (${kLineData.points.length} bars, res=${resolution})`);
    },
    [log, chart],
  );

  const sendPing = useCallback(() => {
    const msg = JSON.stringify({ type: 'ping', payload: { ts: Date.now() } });
    chart()?.postMessage(msg);
    log(`▶ ${msg}`);
  }, [log, chart]);

  // Every prop passed to a Nitro view MUST be referentially stable across
  // renders. The bridge log re-renders this component on each event; if the
  // source object or the wrapped callbacks were rebuilt every render, Nitro
  // would re-apply them to the native view, which re-inits the chart — an
  // onLoadEnd→setState→re-render→re-init feedback loop. useMemo breaks it.
  const source = useMemo(
    () =>
      remote
        ? {
            uri: `https://tradingview.onekey.so/?${new URLSearchParams(
              PARAMS as Record<string, string>,
            ).toString()}`,
          }
        : {
            localBundle: 'tradingview-assets',
            entry: 'index.html',
            paramsJson: JSON.stringify(PARAMS),
          },
    [remote],
  );

  const hybridRefProp = useMemo(
    () =>
      callback((r: ChartHybridRef) => {
        hybridRefHolder.current = r;
      }),
    [],
  );
  const onMessageProp = useMemo(() => callback(onMessage), [onMessage]);
  const onLoadEndProp = useMemo(
    () => callback(() => log('● onLoadEnd')),
    [log],
  );
  const onErrorProp = useMemo(
    () => callback((e: string) => log(`✗ onError: ${e}`)),
    [log],
  );

  return (
    <View style={s.root}>
      <View style={s.toolbar}>
        <TestButton
          title={remote ? 'Source: REMOTE (tap → local)' : 'Source: LOCAL offline (tap → remote)'}
          onPress={() => {
            setLogs([]);
            setRemote((r) => !r);
          }}
        />
        <TestButton title="Send ping → chart" onPress={sendPing} />
        <TestButton title="Reload" onPress={() => chart()?.reload()} />
      </View>

      <ChartWebviewView
        // Remount when source mode flips so the new source loads cleanly.
        key={remote ? 'remote' : 'local'}
        style={s.chart}
        {...source}
        // Methods are reached via `hybridRef` (a wrapped callback), NOT the React
        // `ref`. The callback hands us a ref object whose `.current` is the live
        // HybridObject; we stash it for imperative postMessage()/reload() calls.
        hybridRef={hybridRefProp}
        // Nitro requires every function prop to be wrapped with `callback(...)`,
        // otherwise React Native coerces the function to `true` before it reaches
        // native (which also corrupts the hybrid ref, dropping its methods).
        onMessage={onMessageProp}
        onLoadEnd={onLoadEndProp}
        onError={onErrorProp}
      />

      <View style={s.logBox}>
        <Text style={s.logTitle}>BRIDGE LOG</Text>
        <ScrollView style={s.logScroll} nestedScrollEnabled>
          {logs.length === 0 ? (
            <Text style={s.logEmpty}>
              Idle. Chart should render offline; messages appear here.
            </Text>
          ) : (
            logs.map((l, i) => (
              <Text key={i} style={s.logLine}>
                {l}
              </Text>
            ))
          )}
        </ScrollView>
      </View>
    </View>
  );
}

const s = StyleSheet.create({
  root: { flex: 1, backgroundColor: '#000' },
  toolbar: { padding: 8, gap: 6 },
  chart: { flex: 1, backgroundColor: '#111' },
  logBox: { height: 150, backgroundColor: '#0a0a0a', padding: 8 },
  logTitle: { color: '#8e8e93', fontSize: 10, fontWeight: '700', letterSpacing: 1 },
  logScroll: { flex: 1, marginTop: 4 },
  logEmpty: { color: '#636366', fontSize: 11, fontFamily: mono },
  logLine: { color: '#d1d1d6', fontSize: 10, fontFamily: mono, marginBottom: 2 },
});
