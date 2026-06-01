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

const REUSE_KEY = 'chart-singleton';

// Hyperliquid scene: the chart self-fetches real candles (no business bridge),
// so you can see real state (scroll/zoom) survive the reparent.
const PARAMS = {
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

export function ChartSingletonTestPage() {
  const [slot, setSlot] = useState<'A' | 'B'>('A');
  const [pooled, setPooled] = useState(true);
  const [loads, setLoads] = useState(0);
  const hybridRefHolder = useRef<{ current: ChartWebviewMethods | null } | null>(
    null,
  );

  const source = useMemo(
    () => ({
      localBundle: 'tradingview-assets',
      entry: 'index.html',
      paramsJson: JSON.stringify(PARAMS),
    }),
    [],
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

  // A single chart element. Mounting it inside slot A or slot B is what triggers
  // the native reparent. `key` is stable so React moves (not remounts) it; the
  // native pool is what actually preserves the WebView across slots.
  const chart = (
    <ChartWebviewView
      key="singleton-chart"
      style={s.chart}
      {...source}
      reuseKey={REUSE_KEY}
      pooled={pooled}
      active
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
        <Text style={s.hint}>
          {`slot=${slot}  •  onLoadEnd fired ${loads}×\n`}
          {`adb logcat -s ChartWebviewPool  → count "WebView CREATED"`}
        </Text>
      </View>

      <View style={s.slots}>
        <View style={[s.slot, s.slotA]}>
          <Text style={s.slotLabel}>SLOT A</Text>
          {slot === 'A' ? chart : <Text style={s.empty}>empty</Text>}
        </View>
        <View style={[s.slot, s.slotB]}>
          <Text style={s.slotLabel}>SLOT B</Text>
          {slot === 'B' ? chart : <Text style={s.empty}>empty</Text>}
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
