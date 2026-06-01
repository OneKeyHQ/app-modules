import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, Platform } from 'react-native';
import { callback } from 'react-native-nitro-modules';
import { TestButton } from './TestPageBase';
import { fetchMarketKline } from './marketKline';
import {
  ChartWebviewView,
  type ChartWebviewMethods,
} from '@onekeyfe/react-native-chart-webview';

// Design A — ONE WebView, ONE loaded page, switched between THREE charts of TWO
// kinds with NO reload (true millisecond switch):
//   A·BTC, B·ETH — Hyperliquid (chart self-fetches candles)
//   C·QQQon      — plain market (host bridge-feeds candles)
// The chart loads once under scene=unified, whose UnifiedDatafeed routes per
// source-encoded symbol. Switching a slot just posts SYMBOL_CHANGE{source,...};
// the source prop never changes, so the WebView never reloads.
//
// Loaded from the offline bundle (tradingview-assets/, built from the chart
// repo's unified-datafeed dist) — proves unified instant switching works fully
// offline, not just against the dev server.
const REUSE_KEY = 'chart-singleton';

const MARKET_TOKEN = {
  symbol: 'QQQon',
  networkId: 'evm--56',
  address: '0x0cde6936d305d5b34667fc46425e852efd73559a',
};

// One unified page, initial symbol = encoded 'HL:BTC'. decimal=4 => market
// pricescale 10^(4-2)=100 => 2 decimals (HL uses its own priceScale via bridge).
const UNIFIED_PARAMS: Record<string, string> = {
  scene: 'unified',
  storageNamespace: 'unified',
  type: 'market',
  symbol: 'HL:BTC',
  theme: 'dark',
  locale: 'zh-CN',
  platform: Platform.OS === 'ios' ? 'ios' : 'android',
  appVersion: '6.4.0',
  decimal: '4',
};
const UNIFIED_PARAMS_JSON = JSON.stringify(UNIFIED_PARAMS);

type ISlotKind = 'hyperliquid' | 'market';
interface ISlotDef {
  id: 'A' | 'B' | 'C';
  label: string;
  source: ISlotKind;
  symbol: string;
  networkId?: string;
  address?: string;
}
const SLOTS: ISlotDef[] = [
  { id: 'A', label: 'A · BTC (HL)', source: 'hyperliquid', symbol: 'BTC' },
  { id: 'B', label: 'B · ETH (HL)', source: 'hyperliquid', symbol: 'ETH' },
  {
    id: 'C',
    label: 'C · QQQon (Market)',
    source: 'market',
    symbol: MARKET_TOKEN.symbol,
    networkId: MARKET_TOKEN.networkId,
    address: MARKET_TOKEN.address,
  },
];

// All slots share ONE source (the unified page) so setSource is idempotent and
// the WebView never reloads on switch. Always pass all source keys (empty for
// unused) — a Nitro optional string flipping to absent is rejected as null.
const UNIFIED_SOURCE = {
  uri: '',
  localBundle: 'tradingview-assets',
  entry: 'index.html',
  paramsJson: UNIFIED_PARAMS_JSON,
};

export function ChartSingletonTestPage() {
  const [slot, setSlot] = useState<'A' | 'B' | 'C'>('A');
  const [pooled, setPooled] = useState(true);
  const [loads, setLoads] = useState(0);

  const refs = useRef<(ChartWebviewMethods | null)[]>([null, null, null]);
  const hybridRefs = useMemo(
    () => [0, 1, 2].map((i) => callback((r: ChartWebviewMethods) => { refs.current[i] = r; })),
    [],
  );
  const postToChart = useCallback((msg: string) => {
    const m = refs.current.find((x) => typeof x?.postMessage === 'function');
    m?.postMessage(msg);
  }, []);

  // ONE handler carrying BOTH chart contracts (this is "兼容两种图表" at the
  // bridge): priceScale for Hyperliquid slots, kLineData feed for the market
  // slot (token comes from the request now, routed by the unified datafeed),
  // plus empty marks so nothing waits.
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
            tokenAddress: String(req.address ?? MARKET_TOKEN.address),
            networkId: String(req.networkId ?? MARKET_TOKEN.networkId),
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

  const onLoadEndProp = useMemo(() => callback(() => setLoads((n) => n + 1)), []);

  // Switch = post SYMBOL_CHANGE with the slot's source + token. The source prop
  // never changes => NO reload. The unified datafeed re-resolves to the right
  // source and the chart swaps in <1 frame.
  useEffect(() => {
    const d = SLOTS.find((x) => x.id === slot);
    if (!d) return;
    postToChart(
      JSON.stringify({
        type: 'SYMBOL_CHANGE',
        payload: {
          source: d.source,
          symbol: d.symbol,
          networkId: d.networkId,
          address: d.address,
          displayPair: d.symbol,
          displayCoin: d.symbol,
          force: true,
        },
      }),
    );
  }, [slot, postToChart]);

  const renderChart = (slotIndex: number) => {
    const def = SLOTS[slotIndex];
    return (
      <ChartWebviewView
        style={s.chart}
        {...UNIFIED_SOURCE}
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
        <Text style={s.hint}>
          {`slot=${slot}  •  loads ${loads}× — should STAY 1 (unified: SYMBOL_CHANGE, never reload)\n`}
          {`A/B Hyperliquid (self-feed) · C market (bridge-feed) — one WebView, one page`}
        </Text>
      </View>

      <View style={s.slots}>
        {SLOTS.map((def, i) => (
          <View
            key={def.id}
            style={[s.slot, def.id === 'A' ? s.slotA : def.id === 'B' ? s.slotB : s.slotC]}
          >
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
  hint: { color: '#8e8e93', fontSize: 11, marginTop: 2 },
  slots: { flex: 1 },
  slot: { flex: 1, margin: 6, borderWidth: 1, borderColor: '#333', borderRadius: 8, overflow: 'hidden' },
  slotA: { borderColor: '#0a84ff' },
  slotB: { borderColor: '#ff9f0a' },
  slotC: { borderColor: '#30d158' },
  slotLabel: { color: '#8e8e93', fontSize: 10, fontWeight: '700', padding: 4 },
  chart: { flex: 1, backgroundColor: '#111' },
});
