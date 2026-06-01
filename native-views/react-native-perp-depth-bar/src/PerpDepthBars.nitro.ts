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
  /** Depth percentage per row, 0..100. Length == number of rows on this side. */
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
}

export interface PerpDepthBarsMethods extends HybridViewMethods {}

export type PerpDepthBars = HybridView<PerpDepthBarsProps, PerpDepthBarsMethods>;
