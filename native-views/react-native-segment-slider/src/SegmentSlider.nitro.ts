import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

/**
 * A segmented slider rendered entirely by native code. Track, fill bar, the
 * N+1 segment marks, the draggable thumb and the value bubble are all drawn on
 * the native UI thread (CALayer on iOS, Canvas onDraw on Android) and the pan
 * gesture is handled natively — there is NO JS round-trip per frame during a
 * drag. This mirrors the web variant
 * (`packages/components/src/composite/SegmentSlider/index.tsx`) which avoids
 * React state per frame by mutating the DOM imperatively.
 *
 * Geometry contract (points), matching the web layout 1:1:
 *   THUMB_SIZE = 16, MARK_SIZE = 10, hit/container height = 24
 *   track is inset by THUMB_SIZE/2 on each end → usable width W' = W - 16
 *   pct        = (clamp(value,min,max) - min) / (max - min)
 *   thumb x    = 8 + pct * W'
 *   mark i x   = 8 + (i / segments) * W'   (i in 0..segments)
 *
 * Colors are resolved from the Tamagui theme in the JS wrapper and passed in as
 * hex / rgba() strings so the native side never depends on the theme system.
 */
export interface SegmentSliderProps extends HybridViewProps {
  /** Current (controlled) value. Ignored while the user is dragging. */
  value: number;
  /** Range minimum (default range 0..100). */
  min: number;
  /** Range maximum. */
  max: number;
  /** Number of segment marks. 0 = continuous (no marks). Marks are visual. */
  segments: number;
  /** Track height in points (web default 4). */
  sliderHeight: number;
  /** When true the slider is non-interactive and dimmed. */
  disabled: boolean;
  /** When true a `${round(value)}%` bubble appears above the thumb on drag. */
  showBubble: boolean;
  /** When true the fill grows from the value-0 origin instead of the left. */
  centerOrigin: boolean;
  /**
   * Monotonic token. Bump from JS on coin / leverage reset so the native view
   * snaps to the new value WITHOUT animating the thumb scale. Mirrors the
   * `epoch` convention used by react-native-perp-depth-bar.
   */
  epoch: number;

  // --- Resolved theme colors (hex `#rgb`/`#rrggbb`/`#rrggbbaa` or rgba()) ---
  /** Active fill bar color (Tamagui `bgPrimary`). */
  fillColor: string;
  /** Inactive track color (Tamagui `neutral5`). */
  trackColor: string;
  /** Thumb fill color (Tamagui `bg`). */
  thumbColor: string;
  /** Thumb border color (Tamagui `borderStrong`). */
  thumbBorderColor: string;
  /** Active mark fill + border color (Tamagui `bgPrimary`). */
  markActiveColor: string;
  /** Inactive mark fill color (Tamagui `bg`). */
  markInactiveColor: string;
  /** Inactive mark border color (Tamagui `neutral5`). */
  markBorderColor: string;
  /** Bubble background color (Tamagui `bgPrimary`). */
  bubbleColor: string;
  /** Bubble text color (Tamagui `bg`). */
  bubbleTextColor: string;

  /** Fired (already rounded to an integer) when the value changes. */
  onChange?: (value: number) => void;
  /** Fired once when a drag/tap begins. */
  onSlideStart?: () => void;
  /** Fired once when a drag/tap ends. */
  onSlideComplete?: () => void;
}

export interface SegmentSliderMethods extends HybridViewMethods {}

export type SegmentSlider = HybridView<SegmentSliderProps, SegmentSliderMethods>;
