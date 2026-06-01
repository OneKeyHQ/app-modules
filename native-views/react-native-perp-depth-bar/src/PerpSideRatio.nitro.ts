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

export interface PerpSideRatioMethods extends HybridViewMethods {}

export type PerpSideRatio = HybridView<PerpSideRatioProps, PerpSideRatioMethods>;
