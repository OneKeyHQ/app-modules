import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

/**
 * One native view renders an ENTIRE column of order-book depth bars for a
 * single side (asks or bids). It replaces N reanimated `DepthBar` rows with a
 * single native view that owns N CALayer/onDraw rects and animates each row's
 * horizontal fill on the native UI thread.
 *
 * Layout contract (must mirror the RN text layer for pixel alignment):
 *   row i top  = rowMarginTop + i * (rowHeight + rowMarginTop)
 *   bar rect   = inset vertically by `barInset` inside each row
 * All values are in points (DIP); the native side quantizes to physical pixels.
 */
export interface PerpDepthBarsProps extends HybridViewProps {
  /**
   * Depth percentage per row, 0..100. Length == number of rows on this side.
   *
   * INITIAL VALUE ONLY when using the imperative path. The first
   * `setDepth(buffer)` call switches the view into imperative mode; from then on
   * this prop is IGNORED on every prop-update transaction (so unrelated
   * high-frequency prop commits like `prices`/`sizes` can't wipe the bars —
   * REACT-NATIVE-1JZ). For high-frequency depth updates ALWAYS call `setDepth`;
   * do NOT keep writing this prop at tick rate. Callers that never call
   * `setDepth` may drive the bars purely through this prop (each write retargets).
   */
  percents: number[];
  /** Row height in points (matches the sibling RN text row height). */
  rowHeight: number;
  /** Inter-row gap in points (RN `marginTop`; applied to every row incl. first). */
  rowMarginTop: number;
  /** Vertical inset of the bar inside its row in points (RN `rowHeight - gap`). */
  barInset: number;
  /** Resolved fill color: hex (#rgb/#rrggbb/#rrggbbaa) or rgba()/rgb(). */
  color: string;
  /** Fill anchor / growth direction: 'left' or 'right'. */
  origin: string;
  /** When true, skip animations (accessibility reduce-motion). */
  reducedMotion: boolean;
  /**
   * Monotonic token. Bump from JS on coin switch / tick-size change /
   * empty<->full so native snaps to the new values WITHOUT animating.
   */
  epoch: number;

  /**
   * Per-row price text, parallel to `percents` (same length / order). Drawn
   * natively per row (price left-aligned), so the order-book ladder needs NO
   * sibling RN <Text> rows. Empty => the view renders bars only.
   *
   * INITIAL VALUE ONLY for high-frequency callers: the first `setText` call
   * switches the text layer into imperative mode and this prop is then IGNORED on
   * every prop-update transaction. For high-frequency text updates ALWAYS call
   * `setText`; do NOT keep writing this prop at tick rate (string-array prop
   * commits are the heaviest part of the Fabric props + JSI path — exactly what
   * REACT-NATIVE-1JZ avoids). Callers that update infrequently may drive it
   * purely through this prop.
   */
  prices: string[];
  /**
   * Per-row size text, parallel to `percents`. Drawn natively per row (size
   * right-aligned). Empty => bars only.
   *
   * INITIAL VALUE ONLY for high-frequency callers: update via `setText`; this
   * prop is IGNORED once `setText` has been called (see `prices` —
   * REACT-NATIVE-1JZ).
   */
  sizes: string[];
  /** Price text color: hex or rgba()/rgb(). */
  priceColor: string;
  /** Size text color: hex or rgba()/rgb(). */
  sizeColor: string;
  /** Price text font size in points. */
  priceFontSize: number;
  /** Size text font size in points. */
  sizeFontSize: number;
  /** Horizontal text inset in points (price padding-left, size padding-right). */
  textInset: number;

  /**
   * Fired natively when a row is tapped. `rowIndex` is the 0-based row
   * (top→bottom, parallel to `percents`/`prices`/`sizes`). The view handles
   * the touch itself — no RN overlay / JS hit-testing needed.
   */
  onRowPress?: (rowIndex: number) => void;
}

export interface PerpDepthBarsMethods extends HybridViewMethods {
  /**
   * Imperatively update the per-row depth percentages, bypassing the
   * high-frequency `percents` prop write path (and its props/JSICache lock
   * contention — REACT-NATIVE-1JZ).
   *
   * `buffer` is a packed `Float32Array` (little-endian): one float per row,
   * each the row's depth percentage with the SAME semantics as the
   * `percents: number[]` prop (0..100, clamped natively exactly like the prop
   * setter). Row count == `buffer.byteLength / 4`. The native side rebuilds
   * its animation targets from these values and reuses the identical
   * clamp/target/frame-loop logic as the `percents` prop path — only the data
   * source differs.
   *
   * The `percents` prop is retained for backward compatibility; callers may
   * use either path.
   */
  setDepth(buffer: ArrayBuffer): void;

  /**
   * Imperatively update the per-row price/size text, bypassing the
   * high-frequency `prices`/`sizes` prop write path (and its props/JSICache lock
   * contention — REACT-NATIVE-1JZ). Companion to [setDepth] for the text layer.
   *
   * `prices[i]` / `sizes[i]` are the formatted strings for row `i`, parallel to
   * the depth buffer pushed via `setDepth` (same length / order). Pass `[]` for a
   * side that draws bars only. Call this in the SAME frame/effect as `setDepth`
   * so the bar fill and its row text always come from one source frame (no
   * fill-vs-text misalignment).
   *
   * The `prices`/`sizes` props are retained for backward compatibility; callers
   * may use either path.
   */
  setText(prices: string[], sizes: string[]): void;
}

export type PerpDepthBars = HybridView<PerpDepthBarsProps, PerpDepthBarsMethods>;
