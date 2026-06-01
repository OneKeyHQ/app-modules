import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, Platform, Switch } from 'react-native';
import { callback } from 'react-native-nitro-modules';
import { TestButton } from './TestPageBase';
import { fetchMarketKline } from './marketKline';
import {
  ChartWebviewView,
  type ChartWebviewMethods,
} from '@onekeyfe/react-native-chart-webview';

// ONE shared WebView, reused for THREE charts of TWO different kinds — proving the
// dumb-pipe host carries both chart contracts at once:
//   A · BTC   — Hyperliquid: chart SELF-fetches candles; host only answers the
//   B · ETH     priceScale bridge request. A<->B is instant (SYMBOL_CHANGE, no reload).
//   C · QQQon — plain market: the HOST feeds candles over the bridge
//               (tradingview_getKLineData -> market API -> kLineData reply).
// A/B share one offline chart (same source); switching to/from C is a different
// scene so it reloads. Reparenting moves the live WebView between the 3 slots; the
// inactive slots freeze to their own last frame.
const REUSE_KEY = 'chart-singleton';
const CHART_HOST = 'https://tradingview.onekey.so/';

// --- A/B: Hyperliquid coins (scene self-fetches candles AND supports SYMBOL_CHANGE).
function hyperliquidParams(symbol: string) {
  return {
    symbol,
    type: 'market',
    theme: 'dark',
    locale: 'zh-CN',
    platform: Platform.OS === 'ios' ? 'ios' : 'android',
    appVersion: '6.4.0',
    decimal: '8',
    networkId: 'btc--0',
    address: '',
    scene: 'market-hyperliquid',
    storageNamespace: 'market-hyperliquid',
  };
}

// --- C: plain market token (NO scene => chart uses OnekeyDatafeed => asks the host
// for candles via tradingview_getKLineData). Same token the public market URL uses.
const MARKET_TOKEN = {
  symbol: 'QQQon',
  networkId: 'evm--56',
  address: '0x0cde6936d305d5b34667fc46425e852efd73559a',
};
function marketParams() {
  return {
    symbol: MARKET_TOKEN.symbol,
    type: 'market',
    theme: 'dark',
    locale: 'zh-CN',
    platform: Platform.OS === 'ios' ? 'ios' : 'android',
    appVersion: '6.4.0',
    decimal: '8',
    networkId: MARKET_TOKEN.networkId,
    address: MARKET_TOKEN.address,
    storageNamespace: 'market',
  };
}

type ISlotKind = 'hl' | 'market';
interface ISlotDef {
  id: 'A' | 'B' | 'C';
  label: string;
  kind: ISlotKind;
  symbol: string;
}
const SLOTS: ISlotDef[] = [
  { id: 'A', label: 'A · BTC (HL)', kind: 'hl', symbol: 'BTC' },
  { id: 'B', label: 'B · ETH (HL)', kind: 'hl', symbol: 'ETH' },
  { id: 'C', label: 'C · QQQon (Market)', kind: 'market', symbol: MARKET_TOKEN.symbol },
];

// A & B load the SAME offline chart (Hyperliquid, BTC initial) so A<->B is a pure
// SYMBOL_CHANGE with no reload; C loads the market chart. The chart is loaded once
// per kind — coin B is reached via SYMBOL_CHANGE, never a reload.
const HL_PARAMS_JSON = JSON.stringify(hyperliquidParams('BTC'));
const HL_URL = `${CHART_HOST}?${new URLSearchParams(hyperliquidParams('BTC')).toString()}`;
const MARKET_PARAMS_JSON = JSON.stringify(marketParams());
const MARKET_URL = `${CHART_HOST}?${new URLSearchParams(marketParams()).toString()}`;

// Always pass ALL source keys (unused ones empty): a Nitro optional string prop
// that flips from set to *absent* is reset to null, which the native binding
// rejects ("uri: Value is null, expected a String").
function makeSource(remote: boolean, url: string, paramsJson: string) {
  return remote
    ? { uri: url, localBundle: '', entry: '', paramsJson: '' }
    : { uri: '', localBundle: 'tradingview-assets', entry: 'index.html', paramsJson };
}

export function ChartSingletonTestPage() {
  const [slot, setSlot] = useState<'A' | 'B' | 'C'>('A');
  const [pooled, setPooled] = useState(true);
  const [remote, setRemote] = useState(false);
  const [loads, setLoads] = useState(0);
  const slotRef = useRef<'A' | 'B' | 'C'>('A');
  slotRef.current = slot;

  // The nitro hybridRef callback hands us the HybridObject (methods) directly,
  // once on mount. All 3 hosts share the same pooled WebView, so posting via the
  // active host's methods reaches the live chart.
  const refs = useRef<(ChartWebviewMethods | null)[]>([null, null, null]);
  const hybridRefs = useMemo(
    () => [0, 1, 2].map((i) => callback((r: ChartWebviewMethods) => { refs.current[i] = r; })),
    [],
  );
  const postToChart = useCallback((msg: string) => {
    const m = refs.current.find((x) => typeof x?.postMessage === 'function');
    m?.postMessage(msg);
  }, []);

  const hlSource = useMemo(() => makeSource(remote, HL_URL, HL_PARAMS_JSON), [remote]);
  const marketSource = useMemo(() => makeSource(remote, MARKET_URL, MARKET_PARAMS_JSON), [remote]);

  const postSymbol = useCallback(
    (symbol: string) => {
      postToChart(
        JSON.stringify({
          type: 'SYMBOL_CHANGE',
          payload: { symbol, displayPair: symbol, displayCoin: symbol, force: true },
        }),
      );
    },
    [postToChart],
  );

  // ONE handler carrying BOTH chart contracts at once — this is what "支持两种图表"
  // means at the bridge layer:
  //  • Hyperliquid (A/B): answer tradingview_getHyperliquidPriceScale immediately,
  //    else the chart waits a 5s timeout before rendering.
  //  • plain market (C): the chart asks for candles via tradingview_getKLineData;
  //    fetch them from the market API and reply with kLineData (requestData echoed
  //    verbatim, exactly like the app's handleKLineDataRequest).
  //  • marks (both): answer empty so nothing waits.
  const onMessageProp = useMemo(
    () =>
      callback((raw: string) => {
        let env: { method?: string; data?: any };
        try {
          env = JSON.parse(raw);
        } catch {
          return;
        }
        const method = env?.method;
        if (method === 'tradingview_getHyperliquidPriceScale') {
          postToChart(
            JSON.stringify({
              type: 'HYPERLIQUID_PRICESCALE_RESPONSE',
              payload: { priceScale: 100, minmov: 1, requestId: env?.data?.requestId },
            }),
          );
          return;
        }
        if (method === 'tradingview_getKLineData') {
          const req = env?.data ?? {};
          fetchMarketKline({
            tokenAddress: MARKET_TOKEN.address,
            networkId: MARKET_TOKEN.networkId,
            resolution: String(req.resolution ?? '1'),
            from: Number(req.from),
            to: Number(req.to),
          })
            .then((kLineData) => {
              postToChart(
                JSON.stringify({
                  type: 'kLineData',
                  payload: { type: 'history', kLineData, requestData: req },
                }),
              );
            })
            .catch(() => {});
          return;
        }
        if (method === 'tradingview_getMarks') {
          postToChart(
            JSON.stringify({
              type: 'MARKS_RESPONSE',
              payload: { marks: [], requestId: env?.data?.requestId },
            }),
          );
        }
      }),
    [postToChart],
  );

  const onLoadEndProp = useMemo(
    () =>
      callback(() => {
        setLoads((n) => n + 1);
        // A reload (e.g. arriving at a HL slot from market C) lands on the chart's
        // initial coin; re-assert the active slot's symbol so B shows ETH, not BTC.
        const cur = SLOTS.find((s) => s.id === slotRef.current);
        if (cur?.kind === 'hl') {
          postSymbol(cur.symbol);
        }
      }),
    [postSymbol],
  );

  // On slot change: A<->B is an instant SYMBOL_CHANGE (no reload). Anything to/from
  // C changes the source => the shared WebView reloads to the other chart kind, and
  // the market candle feed / priceScale handler above does the rest.
  useEffect(() => {
    const cur = SLOTS.find((s) => s.id === slot);
    if (cur?.kind === 'hl') {
      postSymbol(cur.symbol);
    }
  }, [slot, postSymbol]);

  // All 3 slots stay mounted; `active` decides which one shows the live WebView vs
  // its own frozen snapshot. A/B share the HL source (one WebView, reparented +
  // SYMBOL_CHANGE); C uses the market source (reload on entry).
  const renderChart = (slotIndex: number) => {
    const def = SLOTS[slotIndex];
    const src = def.kind === 'market' ? marketSource : hlSource;
    return (
      <ChartWebviewView
        style={s.chart}
        {...src}
        reuseKey={REUSE_KEY}
        pooled={pooled}
        active={slot === def.id}
        hybridRef={hybridRefs[slotIndex] as never}
        onMessage={onMessageProp}
        onLoadEnd={onLoadEndProp}
      />
    );
  };

  return (
    <View style={s.root}>
      <View style={s.toolbar}>
        <View style={s.segRow}>
          {SLOTS.map((def) => (
            <TestButton
              key={def.id}
              title={`${def.id}${slot === def.id ? ' ●' : ''}`}
              onPress={() => setSlot(def.id)}
              style={[s.seg, slot === def.id ? s.segActive : s.segIdle]}
            />
          ))}
        </View>
        <TestButton
          title={`Pooled (singleton): ${pooled ? 'ON' : 'OFF'} — tap to toggle`}
          onPress={() => setPooled((p) => !p)}
        />
        <View style={s.switchRow}>
          <Text style={s.switchLabel}>
            Source: {remote ? 'REMOTE url' : 'LOCAL offline bundle'}
          </Text>
          <Switch value={remote} onValueChange={setRemote} />
        </View>
        <TestButton
          title="Clear snapshot cache"
          onPress={() => refs.current.find((x) => x?.clearSnapshot)?.clearSnapshot?.()}
        />
        <Text style={s.hint}>
          {`slot=${slot}  •  loads ${loads}× — A↔B instant (SYMBOL_CHANGE); ↔C reloads (scene change)\n`}
          {`A/B = Hyperliquid (self-feed)   •   C = market (host bridge-feeds candles)`}
        </Text>
      </View>

      <View style={s.slots}>
        {SLOTS.map((def, i) => (
          <View key={def.id} style={[s.slot, def.id === 'A' ? s.slotA : def.id === 'B' ? s.slotB : s.slotC]}>
            <Text style={s.slotLabel}>
              SLOT {def.label} {slot === def.id ? '(live)' : '(snapshot)'}
            </Text>
            {renderChart(i)}
          </View>
        ))}
      </View>
    </View>
  );
}

const s = StyleSheet.create({
  root: { flex: 1, backgroundColor: '#000' },
  toolbar: { padding: 8, gap: 6 },
  segRow: { flexDirection: 'row', gap: 6 },
  seg: { flex: 1, paddingVertical: 10, paddingHorizontal: 4 },
  segActive: { backgroundColor: '#0a84ff' },
  segIdle: { backgroundColor: '#2c2c2e' },
  switchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#1c1c1e',
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 12,
  },
  switchLabel: { color: '#fff', fontSize: 14, fontWeight: '600' },
  hint: { color: '#8e8e93', fontSize: 11, marginTop: 2 },
  slots: { flex: 1 },
  slot: { flex: 1, margin: 6, borderWidth: 1, borderColor: '#333', borderRadius: 8, overflow: 'hidden' },
  slotA: { borderColor: '#0a84ff' },
  slotB: { borderColor: '#ff9f0a' },
  slotC: { borderColor: '#30d158' },
  slotLabel: { color: '#8e8e93', fontSize: 10, fontWeight: '700', padding: 4 },
  chart: { flex: 1, backgroundColor: '#111' },
});
