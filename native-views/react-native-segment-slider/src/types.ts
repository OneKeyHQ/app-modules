/**
 * Resolved color strings (`#rgb`, `#rrggbb`, `#rrggbbaa` or `rgba()`). The
 * package never reads an application theme; callers resolve their theme and
 * pass the values in.
 */
export interface ISegmentSliderColors {
  /** Filled part of the track. */
  fillColor: string;
  /** Unfilled part of the track. */
  trackColor: string;
  /** Thumb fill. */
  thumbColor: string;
  /** Thumb ring. */
  thumbBorderColor: string;
  /** Fill and border of marks the value has reached. */
  markActiveColor: string;
  /** Fill of marks the value has not reached. */
  markInactiveColor: string;
  /** Border of marks the value has not reached. */
  markBorderColor: string;
  /** Value bubble background. */
  bubbleColor: string;
  /** Value bubble text. */
  bubbleTextColor: string;
}

/** Neutral light palette used for any color prop the caller leaves out. */
export const DEFAULT_SEGMENT_SLIDER_COLORS: ISegmentSliderColors = {
  fillColor: '#000000',
  trackColor: '#E3E3E3',
  thumbColor: '#FFFFFF',
  thumbBorderColor: '#B8B8B8',
  markActiveColor: '#000000',
  markInactiveColor: '#FFFFFF',
  markBorderColor: '#E3E3E3',
  bubbleColor: '#000000',
  bubbleTextColor: '#FFFFFF',
};

export interface ISegmentSliderProps extends Partial<ISegmentSliderColors> {
  /** Current value (controlled). */
  'value': number;
  /**
   * Fired with the integer-rounded value when an interaction changes it. The
   * value stays inside the range; with fractional bounds it stays within
   * [ceil(min), floor(max)].
   */
  'onChange': (value: number) => void;
  /** Number of segment intervals; below 1 renders no marks. */
  'segments': number;
  /** Range minimum. Default 0. */
  'min'?: number;
  /** Range maximum. Default 100. */
  'max'?: number;
  /** Track thickness. Default 4. */
  'sliderHeight'?: number;
  /** Dims the slider and blocks interaction. Default false. */
  'disabled'?: boolean;
  /** Shows a `${value}%` bubble above the thumb while interacting. Default true. */
  'showBubble'?: boolean;
  /** Fills from the value-0 origin instead of the left edge. Default false. */
  'centerOrigin'?: boolean;
  /**
   * A quick tap snaps to the nearest mark while a drag stays free; when false
   * a tap lands on the pressed position. No effect without marks. Default
   * true.
   */
  'snapTapToSegment'?: boolean;
  /**
   * iOS / Android: light haptic tick each time a drag moves the value into a
   * new `hapticStep`-sized bucket from `min`. Mark ticks stay stronger. 0 (the
   * default) keeps mark-only ticks. Web has no haptics.
   */
  'hapticStep'?: number;
  /**
   * Web only: places marks on the integer values the slider emits, so keyboard
   * steps and marks agree when the range does not divide evenly. Default false.
   */
  'alignSegmentMarksToIntegerValues'?: boolean;
  /** Accessible name for the slider. */
  'aria-label'?: string;
  /** Id of the element that names the slider, such as a visible field title. */
  'aria-labelledby'?: string;
  'onSlideStart'?: () => void;
  'onSlideComplete'?: () => void;
}
