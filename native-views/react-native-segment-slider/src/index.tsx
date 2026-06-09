import { useCallback, useMemo, useRef } from 'react';

import { type ViewProps } from 'react-native';
import { callback, getHostComponent } from 'react-native-nitro-modules';

import type {
  SegmentSliderMethods,
  SegmentSliderProps,
} from './SegmentSlider.nitro';

const SegmentSliderConfig = require('../nitrogen/generated/shared/json/SegmentSliderConfig.json');

export type { SegmentSliderMethods, SegmentSliderProps };

const SegmentSliderHost = getHostComponent<
  SegmentSliderProps,
  SegmentSliderMethods
>('SegmentSlider', () => SegmentSliderConfig);

/**
 * Thin wrapper so consumers pass plain functions. Nitro view callbacks must be
 * wrapped with `callback(...)` to cross the JSI boundary; we encapsulate that
 * here behind refs so the underlying functions stay stable across re-renders
 * (otherwise every parent render would re-create the native callback).
 */
export function SegmentSliderView({
  onChange,
  onSlideStart,
  onSlideComplete,
  ...props
}: SegmentSliderProps & ViewProps) {
  const onChangeRef = useRef(onChange);
  onChangeRef.current = onChange;
  const onSlideStartRef = useRef(onSlideStart);
  onSlideStartRef.current = onSlideStart;
  const onSlideCompleteRef = useRef(onSlideComplete);
  onSlideCompleteRef.current = onSlideComplete;

  const stableOnChange = useCallback((value: number) => {
    onChangeRef.current?.(value);
  }, []);
  const stableOnSlideStart = useCallback(() => {
    onSlideStartRef.current?.();
  }, []);
  const stableOnSlideComplete = useCallback(() => {
    onSlideCompleteRef.current?.();
  }, []);
  const onChangeCallback = useMemo(
    () => callback(stableOnChange),
    [stableOnChange],
  );
  const onSlideStartCallback = useMemo(
    () => callback(stableOnSlideStart),
    [stableOnSlideStart],
  );
  const onSlideCompleteCallback = useMemo(
    () => callback(stableOnSlideComplete),
    [stableOnSlideComplete],
  );

  return (
    <SegmentSliderHost
      {...props}
      onChange={onChange ? onChangeCallback : undefined}
      onSlideStart={onSlideStart ? onSlideStartCallback : undefined}
      onSlideComplete={onSlideComplete ? onSlideCompleteCallback : undefined}
    />
  );
}
