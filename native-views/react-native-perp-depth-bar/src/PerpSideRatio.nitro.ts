import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

/**
 * Native replacement for `SideRatioSegments`: two horizontal segments whose
 * widths animate to the bid/ask ratio. Outer corners are rounded; a `gap`
 * separates the two segments. Standalone track (not overlaid on text), so
 * alignment risk is low.
 */
export interface PerpSideRatioProps extends HybridViewProps {
  /** Bid (long) share, 0..100. */
  bidPercentage: number;
  /** Ask (short) share, 0..100. */
  askPercentage: number;
  /** Resolved long (bid) color. */
  longColor: string;
  /** Resolved short (ask) color. */
  shortColor: string;
  /** Track/segment height in points. */
  segmentHeight: number;
  /** Outer corner radius in points. */
  cornerRadius: number;
  /** Gap between the two segments in points. */
  gap: number;
  /** When true, skip animations (accessibility reduce-motion). */
  reducedMotion: boolean;
}

export interface PerpSideRatioMethods extends HybridViewMethods {
  /**
   * Imperatively update the bid/ask ratio, bypassing the high-frequency
   * `bidPercentage`/`askPercentage` prop write path (and its props/JSICache
   * lock contention — REACT-NATIVE-1JZ). Companion to `PerpDepthBars.setDepth`.
   *
   * `bidPercentage`/`askPercentage` carry the EXACT SAME semantics as the
   * props of the same name (0..100 shares; the native side derives the split
   * fraction identically). The native side routes these through the same
   * clamp/target/animation path the prop setters use — only the data source
   * differs. The `bidPercentage`/`askPercentage` props are retained for
   * backward compatibility; callers may use either path.
   */
  setRatio(bidPercentage: number, askPercentage: number): void;
}

export type PerpSideRatio = HybridView<PerpSideRatioProps, PerpSideRatioMethods>;
