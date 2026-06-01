import { useCallback, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, Platform } from 'react-native';
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
// slot still freezes to its own last frame when inactive.
const REUSE_KEY = 'chart-singleton';

// A: offline Hyperliquid BTC (self-fetches candles, no business bridge needed).
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

// B: a different chart (QQQon on BSC, market mode). It can be loaded either from
// the REMOTE URL or from the same OFFLINE bundle as A — the toggle below switches
// B's source so you can compare remote vs local loading of the same chart.
const B_URL =
  'https://tradingview.onekey.so/?timezone=Asia%2FShanghai&locale=zh-CN&platform=web&theme=dark&appVersion=6.4.0&decimal=8&networkId=evm--56&address=0x0cde6936d305d5b34667fc46425e852efd73559a&symbol=QQQon&type=market&storageNamespace=market';

// Same params as B_URL, for loading QQQon from the local offline bundle. platform
// is the device so the chart uses our native bridge.
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

export function ChartSingletonTestPage() {
  const [slot, setSlot] = useState<'A' | 'B'>('A');
  const [pooled, setPooled] = useState(true);
  const [bRemote, setBRemote] = useState(true);
  const [loads, setLoads] = useState(0);
  const hybridRefHolder = useRef<{ current: ChartWebviewMethods | null } | null>(
    null,
  );

  // Always pass ALL source keys (unused ones empty): a Nitro optional string
  // prop that flips from set to *absent* gets reset to null, which the native
  // binding rejects ("uri: Value is null, expected a String"). Empty strings
  // keep every prop a valid String; native treats "" as "not this mode".
  const sourceA = useMemo(
    () => ({
      uri: '',
      localBundle: 'tradingview-assets',
      entry: 'index.html',
      paramsJson: JSON.stringify(PARAMS_A),
    }),
    [],
  );
  const sourceB = useMemo(
    () =>
      bRemote
        ? { uri: B_URL, localBundle: '', entry: '', paramsJson: '' }
        : {
            uri: '',
            localBundle: 'tradingview-assets',
            entry: 'index.html',
            paramsJson: JSON.stringify(PARAMS_B),
          },
    [bRemote],
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
        <TestButton
          title={`SLOT B source: ${bRemote ? 'REMOTE url' : 'LOCAL bundle'} — tap to switch`}
          onPress={() => setBRemote((r) => !r)}
        />
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
  hint: { color: '#8e8e93', fontSize: 11, marginTop: 2 },
  slots: { flex: 1 },
  slot: { flex: 1, margin: 6, borderWidth: 1, borderColor: '#333', borderRadius: 8, overflow: 'hidden' },
  slotA: { borderColor: '#0a84ff' },
  slotB: { borderColor: '#ff9f0a' },
  slotLabel: { color: '#8e8e93', fontSize: 10, fontWeight: '700', padding: 4 },
  chart: { flex: 1, backgroundColor: '#111' },
  empty: { color: '#444', fontSize: 12, textAlign: 'center', marginTop: 20 },
});
