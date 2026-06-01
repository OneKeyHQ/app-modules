import { useCallback, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, Platform, Switch } from 'react-native';
import { callback } from 'react-native-nitro-modules';
import { TestButton } from './TestPageBase';
import {
  ChartWebviewView,
  type ChartWebviewMethods,
} from '@onekeyfe/react-native-chart-webview';

// Verifies the singleton: with `pooled` on, the SAME native WebView is reparented
// between mount slots A and B (state preserved, ONE creation). With `pooled` off,
// each slot is its own WebView (recreated, state lost).
//
// How to read the result in logcat:
//   adb logcat -s ChartWebviewPool
//   - pooled ON  -> exactly ONE "WebView CREATED key=chart-singleton" no matter
//     how many times you move A<->B
//   - pooled OFF -> a new "WebView CREATED" every time you move (different keys)
// Visual proof: scroll/zoom the candles, then Move A<->B. Pooled keeps the
// view exactly as you left it; non-pooled reloads from scratch.

// ONE shared WebView (same reuseKey) reused for two DIFFERENT charts: the active
// slot drives the single WebView's content (its params), so switching reloads it
// to the other chart — the real "one chart host, swap symbol" optimization. Each
// slot still freezes to its own last frame when inactive. A single Source switch
// flips BOTH slots between the offline bundle and the remote URL.
const REUSE_KEY = 'chart-singleton';

const CHART_HOST = 'https://tradingview.onekey.so/';

// A: Hyperliquid BTC (self-fetches candles, no business bridge needed).
const PARAMS_A = {
  symbol: 'BTC',
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

// B: a different chart (QQQon on BSC, market mode).
const PARAMS_B = {
  timezone: 'Asia/Shanghai',
  locale: 'zh-CN',
  platform: Platform.OS === 'ios' ? 'ios' : 'android',
  theme: 'dark',
  appVersion: '6.4.0',
  decimal: '8',
  networkId: 'evm--56',
  address: '0x0cde6936d305d5b34667fc46425e852efd73559a',
  symbol: 'QQQon',
  type: 'market',
  storageNamespace: 'market',
};

const PARAMS_A_JSON = JSON.stringify(PARAMS_A);
const PARAMS_B_JSON = JSON.stringify(PARAMS_B);
const URL_A = `${CHART_HOST}?${new URLSearchParams(PARAMS_A).toString()}`;
const URL_B = `${CHART_HOST}?${new URLSearchParams(PARAMS_B).toString()}`;

// Always pass ALL source keys (unused ones empty): a Nitro optional string prop
// that flips from set to *absent* is reset to null, which the native binding
// rejects ("uri: Value is null, expected a String"). Empty strings keep every
// prop a valid String; native treats "" as "not this mode".
function makeSource(remote: boolean, url: string, paramsJson: string) {
  return remote
    ? { uri: url, localBundle: '', entry: '', paramsJson: '' }
    : { uri: '', localBundle: 'tradingview-assets', entry: 'index.html', paramsJson };
}

export function ChartSingletonTestPage() {
  const [slot, setSlot] = useState<'A' | 'B'>('A');
  const [pooled, setPooled] = useState(true);
  const [remote, setRemote] = useState(false);
  const [loads, setLoads] = useState(0);
  const hybridRefHolder = useRef<{ current: ChartWebviewMethods | null } | null>(
    null,
  );

  const sourceA = useMemo(
    () => makeSource(remote, URL_A, PARAMS_A_JSON),
    [remote],
  );
  const sourceB = useMemo(
    () => makeSource(remote, URL_B, PARAMS_B_JSON),
    [remote],
  );

  const hybridRefProp = useMemo(
    () =>
      callback((r: { current: ChartWebviewMethods | null }) => {
        hybridRefHolder.current = r;
      }),
    [],
  );
  const onLoadEndProp = useMemo(
    () => callback(() => setLoads((n) => n + 1)),
    [],
  );

  // Both slots stay mounted; `active` decides which one is shown live vs frozen
  // to its own snapshot. A and B use different reuseKeys, so each is its own
  // persisted WebView with its own content.
  const renderChart = (
    active: boolean,
    source: object,
    reuseKey: string,
  ) => (
    <ChartWebviewView
      style={s.chart}
      {...source}
      reuseKey={reuseKey}
      pooled={pooled}
      active={active}
      hybridRef={hybridRefProp}
      onLoadEnd={onLoadEndProp}
    />
  );

  return (
    <View style={s.root}>
      <View style={s.toolbar}>
        <TestButton
          title={`Move chart  ${slot === 'A' ? 'A → B' : 'B → A'}`}
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
          onPress={() => hybridRefHolder.current?.current?.clearSnapshot()}
        />
        <Text style={s.hint}>
          {`slot=${slot}  •  onLoadEnd fired ${loads}×\n`}
          {`adb logcat -s ChartWebviewPool  → count "WebView CREATED"`}
        </Text>
      </View>

      <View style={s.slots}>
        <View style={[s.slot, s.slotA]}>
          <Text style={s.slotLabel}>SLOT A · BTC {slot === 'A' ? '(live)' : '(snapshot)'}</Text>
          {renderChart(slot === 'A', sourceA, REUSE_KEY)}
        </View>
        <View style={[s.slot, s.slotB]}>
          <Text style={s.slotLabel}>SLOT B · QQQon {slot === 'B' ? '(live)' : '(snapshot)'}</Text>
          {renderChart(slot === 'B', sourceB, REUSE_KEY)}
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
  empty: { color: '#444', fontSize: 12, textAlign: 'center', marginTop: 20 },
});
