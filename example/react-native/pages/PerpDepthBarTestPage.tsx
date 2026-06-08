import { useCallback, useEffect, useRef, useState } from 'react';
import { View, Text, StyleSheet, Switch, Pressable } from 'react-native';
import { TestPageBase, TestButton } from './TestPageBase';
import {
  PerpDepthBarsView,
  PerpSideRatioView,
} from '@onekeyfe/react-native-perp-depth-bar';

const ROW_COUNT = 8;
const ROW_HEIGHT = 28;
const ROW_MARGIN_TOP = 0;
const BAR_INSET = 0;
const COLUMN_WIDTH = 340;
const COLUMN_HEIGHT =
  ROW_MARGIN_TOP + ROW_COUNT * (ROW_HEIGHT + ROW_MARGIN_TOP);

const MID = 1986.75;
const TICK = 0.1;

const ASK_BAR = 'rgba(229, 72, 77, 0.22)';
const BID_BAR = 'rgba(48, 164, 108, 0.22)';
const ASK_TEXT = '#E5484D';
const BID_TEXT = '#30A46C';
const LONG_COLOR = '#30A46C';
const SHORT_COLOR = '#E5484D';

interface Level {
  price: string;
  size: string;
  percent: number;
}

// Build one side's rows in DISPLAY order (top -> bottom).
// asks: top = farthest above mid; bids: top = nearest below mid.
// cumulative depth grows toward the far end, so the bar is widest at the far end.
function genSide(side: 'ask' | 'bid'): Level[] {
  const n = ROW_COUNT;
  const sizes = Array.from({ length: n }, () => Math.random() * 800 + 20);
  const cum = new Array<number>(n);
  let acc = 0;
  if (side === 'ask') {
    // near mid = bottom; accumulate bottom -> top so top is widest
    for (let j = n - 1; j >= 0; j -= 1) {
      acc += sizes[j];
      cum[j] = acc;
    }
  } else {
    // near mid = top; accumulate top -> bottom so bottom is widest
    for (let j = 0; j < n; j += 1) {
      acc += sizes[j];
      cum[j] = acc;
    }
  }
  const max = Math.max(...cum) || 1;
  return sizes.map((s, j) => {
    const price =
      side === 'ask' ? MID + (n - j) * TICK : MID - (j + 1) * TICK;
    return {
      price: price.toFixed(1),
      size: s.toFixed(2),
      percent: Math.round((cum[j] / max) * 100),
    };
  });
}

function Side({
  side,
  levels,
  barColor,
  textColor,
  origin,
  reducedMotion,
  epoch,
  onSelectRow,
}: {
  side: 'ask' | 'bid';
  levels: Level[];
  barColor: string;
  textColor: string;
  origin: 'left' | 'right';
  reducedMotion: boolean;
  epoch: number;
  onSelectRow?: (level: Level, side: 'ask' | 'bid', index: number) => void;
}) {
  // Bars, price/size text AND per-row tap are all handled natively — the
  // view owns the touch and emits `onRowPress(rowIndex)`. Plain function —
  // the module wraps it with Nitro's `callback()` internally.
  const levelsRef = useRef(levels);
  levelsRef.current = levels;
  const handleRowPress = useCallback(
    (rowIndex: number) => {
      const l = levelsRef.current[rowIndex];
      if (l) onSelectRow?.(l, side, rowIndex);
    },
    [onSelectRow, side],
  );
  return (
    <View style={{ width: COLUMN_WIDTH, height: COLUMN_HEIGHT }}>
      <PerpDepthBarsView
        style={StyleSheet.absoluteFill}
        percents={levels.map((l) => l.percent)}
        rowHeight={ROW_HEIGHT}
        rowMarginTop={ROW_MARGIN_TOP}
        barInset={BAR_INSET}
        color={barColor}
        origin={origin}
        reducedMotion={reducedMotion}
        epoch={epoch}
        prices={levels.map((l) => l.price)}
        sizes={levels.map((l) => l.size)}
        priceColor={textColor}
        sizeColor="#c9d1d9"
        priceFontSize={13}
        sizeFontSize={13}
        textInset={8}
        placeholderText="--"
        placeholderRows={levels.length}
        onRowPress={handleRowPress}
      />
    </View>
  );
}

export function PerpDepthBarTestPage() {
  const [asks, setAsks] = useState<Level[]>(() => genSide('ask'));
  const [bids, setBids] = useState<Level[]>(() => genSide('bid'));
  const [bidShare, setBidShare] = useState(37);
  const [reducedMotion, setReducedMotion] = useState(false);
  const [epoch, setEpoch] = useState(0);
  const [auto, setAuto] = useState(true);
  const [tapped, setTapped] = useState('—');
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const tick = () => {
    setAsks(genSide('ask'));
    setBids(genSide('bid'));
    setBidShare(Math.round(Math.random() * 70 + 15));
  };

  useEffect(() => {
    if (auto) {
      timerRef.current = setInterval(tick, 600);
    }
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      timerRef.current = null;
    };
  }, [auto]);

  return (
    <TestPageBase title="Perp Depth Bar">
      <View style={styles.controls}>
        <View style={styles.switchRow}>
          <Text style={styles.label}>Auto tick (600ms)</Text>
          <Switch value={auto} onValueChange={setAuto} />
        </View>
        <View style={styles.switchRow}>
          <Text style={styles.label}>Reduced motion (snap)</Text>
          <Switch value={reducedMotion} onValueChange={setReducedMotion} />
        </View>
        <TestButton title="Single tick" onPress={tick} />
        <TestButton
          title="Reset (bump epoch — snap, no anim)"
          onPress={() => {
            setEpoch((e) => e + 1);
            tick();
          }}
        />
      </View>

      <View style={styles.switchRow}>
        <Text style={styles.label}>Tapped row</Text>
        <Text style={[styles.label, { fontWeight: '700' }]}>{tapped}</Text>
      </View>

      <View style={styles.book}>
        {/* two-line stacked header (matches production: 价格/(USD), 数量/(MU)) */}
        <View style={styles.header}>
          <View>
            <Text style={styles.headerCell}>价格</Text>
            <Text style={styles.headerUnit}>(USD)</Text>
          </View>
          <View style={{ alignItems: 'flex-end' }}>
            <Text style={styles.headerCell}>数量</Text>
            <Text style={styles.headerUnit}>(ETH)</Text>
          </View>
        </View>

        <Side
          side="ask"
          levels={asks}
          barColor={ASK_BAR}
          textColor={ASK_TEXT}
          origin="left"
          reducedMotion={reducedMotion}
          epoch={epoch}
          onSelectRow={(l, s) => setTapped(`${s.toUpperCase()} ${l.price}  ×${l.size}`)}
        />

        {/* mid / spread block — big mark price + small last price, clickable.
            Left padding lives on the block so both lines share one left
            origin, aligned with the price column (native textInset = 8). */}
        <Pressable
          style={styles.midBlock}
          onPress={() => setTapped(`MID ${MID.toFixed(2)}`)}
        >
          <Text style={styles.mid}>{MID.toFixed(2)}</Text>
          <Text style={styles.midSub}>{(MID - TICK).toFixed(1)}</Text>
        </Pressable>

        <Side
          side="bid"
          levels={bids}
          barColor={BID_BAR}
          textColor={BID_TEXT}
          origin="left"
          reducedMotion={reducedMotion}
          epoch={epoch}
          onSelectRow={(l, s) => setTapped(`${s.toUpperCase()} ${l.price}  ×${l.size}`)}
        />

        <View style={styles.ratioWrap}>
          <Text style={[styles.ratioLabel, { color: BID_TEXT }]}>
            B {bidShare}%
          </Text>
          <PerpSideRatioView
            style={styles.ratio}
            bidPercentage={bidShare}
            askPercentage={100 - bidShare}
            longColor={LONG_COLOR}
            shortColor={SHORT_COLOR}
            segmentHeight={4}
            cornerRadius={999}
            gap={2}
            reducedMotion={reducedMotion}
          />
          <Text style={[styles.ratioLabel, { color: ASK_TEXT }]}>
            {100 - bidShare}% S
          </Text>
        </View>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  controls: { gap: 12, marginBottom: 8 },
  switchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  label: { fontSize: 15, color: '#333' },
  book: {
    backgroundColor: '#0b0e11',
    borderRadius: 8,
    paddingVertical: 8,
    paddingHorizontal: 4,
    alignItems: 'center',
  },
  header: {
    width: COLUMN_WIDTH,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 8,
    paddingBottom: 6,
  },
  headerCell: { fontSize: 13, color: '#8b949e' },
  headerUnit: { fontSize: 11, color: '#8b949e' },
  row: {
    height: ROW_HEIGHT,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 8,
  },
  cell: {
    fontSize: 13,
    fontVariant: ['tabular-nums'],
  },
  sizeCell: { color: '#c9d1d9', textAlign: 'right' },
  midBlock: {
    width: COLUMN_WIDTH,
    paddingLeft: 8,
    paddingVertical: 6,
    alignItems: 'flex-start',
  },
  mid: {
    fontSize: 22,
    fontWeight: '600',
    color: '#e6edf3',
  },
  midSub: {
    fontSize: 12,
    color: '#8b949e',
    // dashed underline (RN Android ignores textDecorationStyle, so use a
    // dashed bottom border sized to the text)
    borderBottomWidth: 1,
    borderBottomColor: '#8b949e',
    borderStyle: 'dashed',
    paddingBottom: 1,
  },
  ratioWrap: {
    width: COLUMN_WIDTH,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 8,
    paddingTop: 10,
    gap: 8,
  },
  ratioLabel: { fontSize: 12, fontWeight: '600' },
  ratio: { flex: 1, height: 8 },
});
