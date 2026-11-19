import { memo, useCallback, useLayoutEffect, useMemo, useRef } from 'react';
import type { ComponentProps, ComponentType } from 'react';

import { StyleSheet } from 'react-native';
import {
  type HybridRef,
  type NitroViewWrappedCallback,
  callback,
} from 'react-native-nitro-modules';

import { SegmentSliderView } from './SegmentSliderView';
import { DEFAULT_SEGMENT_SLIDER_COLORS } from './types';

import type {
  SegmentSliderMethods,
  SegmentSliderProps,
} from './SegmentSlider.nitro';
import type { ISegmentSliderProps } from './types';

const HIT_AREA_HEIGHT = 24;
const DEFAULT_TRACK_HEIGHT = 4;

type ISegmentSliderHybridRef = HybridRef<
  SegmentSliderProps,
  SegmentSliderMethods
>;

// `SegmentSliderView` types its props without the Nitro-injected `hybridRef`;
// re-add it (wrapped with `callback(...)` to cross JSI) to reach `setValue`.
type ISegmentSliderNativeViewProps = ComponentProps<
  typeof SegmentSliderView
> & {
  hybridRef?: NitroViewWrappedCallback<(ref: ISegmentSliderHybridRef) => void>;
};

const SegmentSliderNativeView =
  SegmentSliderView as ComponentType<ISegmentSliderNativeViewProps>;

const styles = StyleSheet.create({
  slider: {
    width: '100%',
    height: HIT_AREA_HEIGHT,
  },
});

/**
 * Controlled wrapper around the uncontrolled Nitro view. The native view takes
 * its first value through `defaultValue` and owns the value afterwards; this
 * wrapper pushes later `value` changes through `setValue` on the hybrid ref.
 * The push is skipped while the user drags (so the parent echoing our own
 * `onChange` can't yank the thumb) and for no-op updates.
 */
function SegmentSliderComponent({
  value,
  onChange,
  segments,
  min = 0,
  max = 100,
  sliderHeight = DEFAULT_TRACK_HEIGHT,
  disabled = false,
  showBubble = true,
  centerOrigin = false,
  snapTapToSegment = false,
  hapticStep = 0,
  onSlideStart,
  onSlideComplete,
  'aria-label': ariaLabel,
  'aria-labelledby': ariaLabelledBy,
  fillColor = DEFAULT_SEGMENT_SLIDER_COLORS.fillColor,
  trackColor = DEFAULT_SEGMENT_SLIDER_COLORS.trackColor,
  thumbColor = DEFAULT_SEGMENT_SLIDER_COLORS.thumbColor,
  thumbBorderColor = DEFAULT_SEGMENT_SLIDER_COLORS.thumbBorderColor,
  markActiveColor = DEFAULT_SEGMENT_SLIDER_COLORS.markActiveColor,
  markInactiveColor = DEFAULT_SEGMENT_SLIDER_COLORS.markInactiveColor,
  markBorderColor = DEFAULT_SEGMENT_SLIDER_COLORS.markBorderColor,
  bubbleColor = DEFAULT_SEGMENT_SLIDER_COLORS.bubbleColor,
  bubbleTextColor = DEFAULT_SEGMENT_SLIDER_COLORS.bubbleTextColor,
}: ISegmentSliderProps) {
  // Latest external `value`, so the hybridRef callback can push it once the
  // native view attaches even if `value` changed before that.
  const latestValueRef = useRef(value);
  // Last value the native side reported or that we pushed in; skips no-ops.
  const lastValueRef = useRef(value);
  // True between onSlideStart/onSlideComplete; gates the value sync.
  const draggingRef = useRef(false);
  const lastCompletedInteractionIdRef = useRef(0);

  const sliderRef = useRef<ISegmentSliderHybridRef | null>(null);
  const hybridRef = useMemo(
    () =>
      callback((node: ISegmentSliderHybridRef) => {
        // Android's generated Nitro updater never clears hybridRef.isDirty, so
        // this re-fires on every props commit there (iOS only on attach). A
        // same-node re-fire must be a no-op to preserve interaction state.
        if (sliderRef.current === node) {
          return;
        }
        sliderRef.current = node;
        // A fresh view cannot be mid-drag, and a teardown during a drag never
        // delivers onSlideComplete, so clear the flag here.
        draggingRef.current = false;
        lastCompletedInteractionIdRef.current = 0;
        // `defaultValue` only captures the first value; a rebuilt native view
        // (remount, Fabric recycling) may sit at a stale position. Push the
        // latest value unconditionally — `setValue` is idempotent.
        lastValueRef.current = latestValueRef.current;
        node.setValue(latestValueRef.current, 0);
      }),
    []
  );

  // Captured once for the uncontrolled native view.
  const initialValueRef = useRef(value);

  const handleChange = useCallback(
    (next: number) => {
      lastValueRef.current = next;
      onChange(next);
    },
    [onChange]
  );

  const handleSlideStart = useCallback(() => {
    draggingRef.current = true;
    onSlideStart?.();
  }, [onSlideStart]);

  const handleSlideComplete = useCallback(
    (interactionId: number, finalValue: number) => {
      draggingRef.current = false;
      lastCompletedInteractionIdRef.current = interactionId;
      // Native reports the actual value because an earlier sync could have
      // arrived during a newer gesture and been dropped.
      lastValueRef.current = finalValue;
      const node = sliderRef.current;
      const acceptedValue = latestValueRef.current;
      if (node && acceptedValue !== finalValue) {
        lastValueRef.current = acceptedValue;
        node.setValue(acceptedValue, interactionId);
      }
      onSlideComplete?.();
    },
    [onSlideComplete]
  );

  useLayoutEffect(() => {
    latestValueRef.current = value;
    if (draggingRef.current) return;
    if (value === lastValueRef.current) return;
    const node = sliderRef.current;
    if (!node) {
      // Leave lastValueRef stale so the hybridRef callback pushes this value.
      return;
    }
    lastValueRef.current = value;
    node.setValue(value, lastCompletedInteractionIdRef.current);
  }, [value]);

  return (
    <SegmentSliderNativeView
      style={styles.slider}
      aria-label={ariaLabel}
      aria-labelledby={ariaLabelledBy}
      hybridRef={hybridRef}
      defaultValue={initialValueRef.current}
      min={min}
      max={max}
      segments={segments}
      sliderHeight={sliderHeight}
      disabled={disabled}
      showBubble={showBubble}
      centerOrigin={centerOrigin}
      snapTapToSegment={snapTapToSegment}
      hapticStep={hapticStep}
      fillColor={fillColor}
      trackColor={trackColor}
      thumbColor={thumbColor}
      thumbBorderColor={thumbBorderColor}
      markActiveColor={markActiveColor}
      markInactiveColor={markInactiveColor}
      markBorderColor={markBorderColor}
      bubbleColor={bubbleColor}
      bubbleTextColor={bubbleTextColor}
      onChange={handleChange}
      onSlideStart={handleSlideStart}
      onSlideComplete={handleSlideComplete}
    />
  );
}

export const SegmentSlider = memo(SegmentSliderComponent);
SegmentSlider.displayName = 'SegmentSlider';
