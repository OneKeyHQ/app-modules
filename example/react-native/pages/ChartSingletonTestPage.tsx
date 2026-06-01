import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, Platform, Switch } from 'react-native';
import { callback } from 'react-native-nitro-modules';
import { TestButton } from './TestPageBase';
import {
  ChartWebviewView,
  type ChartWebviewMethods,
} from '@onekeyfe/react-native-chart-webview';

// ONE shared WebView, reused for two DIFFERENT market charts WITHOUT reloading:
// both slots load the same market chart once, and switching sends a SYMBOL_CHANGE
// message so the chart swaps symbol via tvWidget.setSymbol (no page/library
// reload — instant), then re-requests candles which we feed from the market API.
// Reparenting moves the live WebView between slots; the inactive slot freezes to
// its own last frame.
const REUSE_KEY = 'chart-singleton';
const CHART_HOST = 'https://tradingview.onekey.so/';

// Two Hyperliquid coins. The Hyperliquid scene self-fetches candles AND supports
// SYMBOL_CHANGE (perps-style), so switching coins is instant — no reload, no
// bridge data feed needed.
const TOKEN_A = { symbol: 'BTC' };
const TOKEN_B = { symbol: 'ETH' };

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

// The single chart is loaded once with coin A; coin B is reached via
// SYMBOL_CHANGE, never a reload.
const SHARED_PARAMS_JSON = JSON.stringify(hyperliquidParams(TOKEN_A.symbol));
const SHARED_URL = `${CHART_HOST}?${new URLSearchParams(hyperliquidParams(TOKEN_A.symbol)).toString()}`;

// Always pass ALL source keys (unused ones empty): a Nitro optional string prop
// that flips from set to *absent* is reset to null, which the native binding
// rejects ("uri: Value is null, expected a String").
function makeSource(remote: boolean) {
  return remote
    ? { uri: SHARED_URL, localBundle: '', entry: '', paramsJson: '' }
    : {
        uri: '',
        localBundle: 'tradingview-assets',
        entry: 'index.html',
        paramsJson: SHARED_PARAMS_JSON,
      };
}

export function ChartSingletonTestPage() {
  const [slot, setSlot] = useState<'A' | 'B'>('A');
  const [pooled, setPooled] = useState(true);
  const [remote, setRemote] = useState(false);
  const [loads, setLoads] = useState(0);

  // The nitro hybridRef callback hands us the HybridObject (methods) directly,
  // once on mount. Both hosts share the same pooled WebView, so posting via
  // either reaches the live chart.
  const refA = useRef<ChartWebviewMethods | null>(null);
  const refB = useRef<ChartWebviewMethods | null>(null);
  const hybridRefA = useMemo(
    () => callback((r: ChartWebviewMethods) => { refA.current = r; }),
    [],
  );
  const hybridRefB = useMemo(
    () => callback((r: ChartWebviewMethods) => { refB.current = r; }),
    [],
  );
  const postToChart = useCallback((msg: string) => {
    const m = [refA.current, refB.current].find(
      (x) => typeof x?.postMessage === 'function',
    );
    m?.postMessage(msg);
  }, []);

  const source = useMemo(() => makeSource(remote), [remote]);

  const onLoadEndProp = useMemo(
    () => callback(() => setLoads((n) => n + 1)),
    [],
  );

  // On slot change, swap the chart's symbol via SYMBOL_CHANGE — the chart calls
  // tvWidget.setSymbol (no page/library reload = instant), then self-fetches the
  // new coin's candles (Hyperliquid scene).
  useEffect(() => {
    const token = slot === 'A' ? TOKEN_A : TOKEN_B;
    postToChart(
      JSON.stringify({
        type: 'SYMBOL_CHANGE',
        payload: {
          symbol: token.symbol,
          displayPair: token.symbol,
          displayCoin: token.symbol,
          force: true,
        },
      }),
    );
  }, [slot, postToChart]);

  // Both slots stay mounted; `active` decides which one shows the live WebView vs
  // its own frozen snapshot. Same source + reuseKey => one WebView, reparented.
  const renderChart = (active: boolean, hybridRef: unknown) => (
    <ChartWebviewView
      style={s.chart}
      {...source}
      reuseKey={REUSE_KEY}
      pooled={pooled}
      active={active}
      hybridRef={hybridRef as never}
      onLoadEnd={onLoadEndProp}
    />
  );

  return (
    <View style={s.root}>
      <View style={s.toolbar}>
        <TestButton
          title={`Switch symbol  ${slot === 'A' ? `${TOKEN_A.symbol} → ${TOKEN_B.symbol}` : `${TOKEN_B.symbol} → ${TOKEN_A.symbol}`}  (instant)`}
          onPress={() => setSlot((p) => (p === 'A' ? 'B' : 'A'))}
        />
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
          onPress={() => (refA.current ?? refB.current)?.clearSnapshot?.()}
        />
        <Text style={s.hint}>
          {`slot=${slot} (${slot === 'A' ? TOKEN_A.symbol : TOKEN_B.symbol})  •  loads ${loads}× — should stay low (SYMBOL_CHANGE, not reload)\n`}
          {`adb logcat -s ChartWebviewPool  → count "WebView CREATED"`}
        </Text>
      </View>

      <View style={s.slots}>
        <View style={[s.slot, s.slotA]}>
          <Text style={s.slotLabel}>SLOT A · {TOKEN_A.symbol} {slot === 'A' ? '(live)' : '(snapshot)'}</Text>
          {renderChart(slot === 'A', hybridRefA)}
        </View>
        <View style={[s.slot, s.slotB]}>
          <Text style={s.slotLabel}>SLOT B · {TOKEN_B.symbol} {slot === 'B' ? '(live)' : '(snapshot)'}</Text>
          {renderChart(slot === 'B', hybridRefB)}
        </View>
      </View>
    </View>
  );
}

const s = StyleSheet.create({
  root: { flex: 1, backgroundColor: '#000' },
  toolbar: { padding: 8, gap: 6 },
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
  slotLabel: { color: '#8e8e93', fontSize: 10, fontWeight: '700', padding: 4 },
  chart: { flex: 1, backgroundColor: '#111' },
});
