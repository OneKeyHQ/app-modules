import {
  memo,
  useCallback,
  useEffect,
  useLayoutEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import type {
  CSSProperties,
  FocusEvent as ReactFocusEvent,
  KeyboardEvent as ReactKeyboardEvent,
  PointerEvent as ReactPointerEvent,
} from 'react';

import { DEFAULT_SEGMENT_SLIDER_COLORS } from './types';

import type { ISegmentSliderProps } from './types';

const THUMB_SIZE = 16;
const MARK_SIZE = 10;
// Square hit area around each mark so mouse/touch don't need pixel precision.
const MARK_HIT_AREA = 24;
const DEFAULT_TRACK_HEIGHT = 4;
const HIT_AREA_HEIGHT = 24;
const MARK_Z_INDEX = 3;
// Above the mark hit areas so the thumb covers the mark it sits on. The thumb
// has pointer-events: none, so the marks underneath stay clickable.
const THUMB_Z_INDEX = 4;
// Tap-vs-drag thresholds for `snapTapToSegment`, matching native.
const TAP_MOVE_THRESHOLD = 8;
const TAP_MAX_DURATION_MS = 300;
// A tap's press is ~100ms, too short to read the bubble, so after a tap the
// bubble stays up for this long before fading. Drags still hide on release.
const TAP_BUBBLE_LINGER_MS = 800;

// Clamps an integer result into [ceil(min), floor(max)] so fractional bounds
// never produce a value past them.
function clampToIntegerRange(value: number, min: number, max: number) {
  const lo = Math.ceil(min);
  const hi = Math.floor(max);
  if (lo > hi) return value;
  return Math.max(lo, Math.min(hi, value));
}

// Keep half-step marks symmetric when the slider emits integer values.
function roundHalfToEven(value: number) {
  const lower = Math.floor(value);
  if (value - lower !== 0.5) {
    return Math.round(value);
  }
  return lower % 2 === 0 ? lower : lower + 1;
}

interface ISegmentMarkProps {
  index: number;
  pct: number;
  hoverGlowColor: string;
  disabled: boolean;
  registerRef: (index: number, el: HTMLDivElement | null) => void;
}

const SegmentMark = memo(
  ({
    index,
    pct,
    hoverGlowColor,
    disabled,
    registerRef,
  }: ISegmentMarkProps) => {
    const [hovered, setHovered] = useState(false);

    const handleRef = useCallback(
      (el: HTMLDivElement | null) => registerRef(index, el),
      [index, registerRef]
    );

    const handleMouseEnter = useCallback(() => {
      if (!disabled) setHovered(true);
    }, [disabled]);
    const handleMouseLeave = useCallback(() => setHovered(false), []);

    const hitAreaStyle = useMemo<CSSProperties>(
      () => ({
        position: 'absolute',
        top: '50%',
        left: `${pct}%`,
        transform: 'translate(-50%, -50%)',
        width: MARK_HIT_AREA,
        height: MARK_HIT_AREA,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        cursor: disabled ? 'not-allowed' : 'pointer',
        // The track has pointer-events: none; marks opt back in for hover/click.
        pointerEvents: 'auto',
        zIndex: MARK_Z_INDEX,
      }),
      [pct, disabled]
    );

    // background/borderColor are owned by applyMarkActiveStates (imperative);
    // listing them here would let a hover re-render clobber the active fill.
    // borderWidth/borderStyle must stay here or the ring never renders.
    const visualStyle = useMemo<CSSProperties>(
      () => ({
        width: MARK_SIZE,
        height: MARK_SIZE,
        borderRadius: '50%',
        borderWidth: 1,
        borderStyle: 'solid',
        borderColor: 'transparent',
        boxSizing: 'border-box',
        boxShadow: hovered ? `0 0 0 4px ${hoverGlowColor}` : 'none',
        transition: 'box-shadow 150ms ease',
      }),
      [hovered, hoverGlowColor]
    );

    return (
      <div
        data-segment-mark={index}
        style={hitAreaStyle}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        <div ref={handleRef} style={visualStyle} />
      </div>
    );
  }
);
SegmentMark.displayName = 'SegmentMark';

/**
 * Web SegmentSlider. Pure DOM: during a drag, thumb/fill/marks/bubble styles
 * are mutated imperatively instead of re-rendering React per frame (avoids
 * hysteresis, refresh layout races and segment click drift).
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
  alignSegmentMarksToIntegerValues = false,
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
  const containerRef = useRef<HTMLDivElement>(null);
  const trackRef = useRef<HTMLDivElement>(null);
  const fillRef = useRef<HTMLDivElement>(null);
  const thumbRef = useRef<HTMLDivElement>(null);
  const bubbleRef = useRef<HTMLDivElement>(null);
  const markRefs = useRef<(HTMLDivElement | null)[]>([]);
  const draggingRef = useRef(false);
  const pointerIdRef = useRef<number | null>(null);
  const lastEmittedRef = useRef<number>(value);
  // Latest `value` from the parent, reconciled when an interaction ends so a
  // value the parent clamped or reset mid-drag is not lost.
  const latestValueRef = useRef<number>(value);
  // Tap-vs-drag tracking for `snapTapToSegment`.
  const pressStartXRef = useRef(0);
  const pressLastXRef = useRef(0);
  const pressStartTimeRef = useRef(0);
  const hasDraggedRef = useRef(false);
  // True when the active press started on a mark (already snapped to it).
  const pressedMarkRef = useRef(false);
  const bubbleHideTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  // Value of the tap whose bubble is lingering or fading; null otherwise.
  const bubbleOwnerValueRef = useRef<number | null>(null);
  const [focusVisible, setFocusVisible] = useState(false);

  const range = max - min;
  const safeRange = range || 1;
  // segments < 1 disables marks and snapping (continuous slider).
  const hasSegments = segments >= 1;
  const stepValue = hasSegments ? range / segments : 0;
  const snapTapsActive = snapTapToSegment && hasSegments;

  const valueToPct = useCallback(
    (v: number) => {
      const clamped = Math.max(min, Math.min(max, v));
      return ((clamped - min) / safeRange) * 100;
    },
    [min, max, safeRange]
  );

  const centerPct = useMemo(() => valueToPct(0), [valueToPct]);

  const segmentIndexToValue = useCallback(
    (index: number) => {
      const segmentValue = min + index * stepValue;
      return clampToIntegerRange(
        alignSegmentMarksToIntegerValues
          ? roundHalfToEven(segmentValue)
          : Math.round(segmentValue),
        min,
        max
      );
    },
    [alignSegmentMarksToIntegerValues, min, max, stepValue]
  );

  const segmentIndexToPct = useCallback(
    (index: number) =>
      alignSegmentMarksToIntegerValues
        ? valueToPct(segmentIndexToValue(index))
        : (index / segments) * 100,
    [
      alignSegmentMarksToIntegerValues,
      segmentIndexToValue,
      segments,
      valueToPct,
    ]
  );

  const applyMarkActiveStates = useCallback(
    (v: number) => {
      if (!hasSegments) return;
      const valuePct = valueToPct(v);
      markRefs.current.forEach((el, idx) => {
        if (!el) return;
        const markPct = segmentIndexToPct(idx);
        let active = false;
        if (centerOrigin) {
          if (v === 0) {
            active = false;
          } else if (v > 0) {
            active = markPct > centerPct && markPct <= valuePct;
          } else {
            active = markPct >= valuePct && markPct < centerPct;
          }
        } else {
          active = markPct <= valuePct;
        }
        // Inactive marks get an opaque fill so the track doesn't bleed through.
        el.style.background = active ? markActiveColor : markInactiveColor;
        el.style.borderColor = active ? markActiveColor : markBorderColor;
      });
    },
    [
      hasSegments,
      valueToPct,
      segmentIndexToPct,
      centerOrigin,
      centerPct,
      markActiveColor,
      markInactiveColor,
      markBorderColor,
    ]
  );

  const applyVisual = useCallback(
    (v: number) => {
      const pct = valueToPct(v);
      const thumbEl = thumbRef.current;
      if (thumbEl) thumbEl.style.left = `${pct}%`;

      const fill = fillRef.current;
      if (fill) {
        if (centerOrigin) {
          if (v === 0) {
            fill.style.width = '0%';
          } else {
            const left = Math.min(pct, centerPct);
            const width = Math.abs(pct - centerPct);
            fill.style.left = `${left}%`;
            fill.style.width = `${width}%`;
          }
        } else {
          fill.style.left = '0%';
          fill.style.width = `${pct}%`;
        }
      }

      const bubble = bubbleRef.current;
      if (bubble) {
        bubble.style.left = `${pct}%`;
        bubble.textContent = `${Math.round(v)}%`;
      }

      applyMarkActiveStates(v);
    },
    [valueToPct, centerOrigin, centerPct, applyMarkActiveStates]
  );

  const clearBubbleHideTimer = useCallback(() => {
    if (bubbleHideTimerRef.current !== null) {
      clearTimeout(bubbleHideTimerRef.current);
      bubbleHideTimerRef.current = null;
    }
  }, []);

  const setBubbleVisible = useCallback(
    (visible: boolean) => {
      clearBubbleHideTimer();
      bubbleOwnerValueRef.current = null;
      const bubble = bubbleRef.current;
      if (!bubble) return;
      bubble.style.opacity = visible ? '1' : '0';
    },
    [clearBubbleHideTimer]
  );

  // Removes the bubble at once, so it never fades out showing a value that
  // did not come from the tap that showed it.
  const hideBubbleNow = useCallback(() => {
    clearBubbleHideTimer();
    bubbleOwnerValueRef.current = null;
    const bubble = bubbleRef.current;
    if (!bubble) return;
    const { transition } = bubble.style;
    bubble.style.transition = 'none';
    bubble.style.opacity = '0';
    // Flush the style so the opacity change skips the transition.
    bubble.getBoundingClientRect();
    bubble.style.transition = transition;
  }, [clearBubbleHideTimer]);

  // Shows the bubble (with the value the tap landed on), then fades it out
  // after TAP_BUBBLE_LINGER_MS. A new press cancels the pending fade.
  const lingerThenHideBubble = useCallback(
    (tapValue: number) => {
      setBubbleVisible(true);
      bubbleOwnerValueRef.current = tapValue;
      bubbleHideTimerRef.current = setTimeout(() => {
        bubbleHideTimerRef.current = null;
        const bubble = bubbleRef.current;
        if (bubble) bubble.style.opacity = '0';
      }, TAP_BUBBLE_LINGER_MS);
    },
    [setBubbleVisible]
  );

  useEffect(() => clearBubbleHideTimer, [clearBubbleHideTimer]);

  const reconcileValue = useCallback(() => {
    const acceptedValue = latestValueRef.current;
    lastEmittedRef.current = acceptedValue;
    applyVisual(acceptedValue);
  }, [applyVisual]);

  // Initial paint, external value sync and color repaint. Layout effect so the
  // thumb is in place on first paint. Skipped while dragging so the thumb
  // doesn't fight the pointer.
  useLayoutEffect(() => {
    latestValueRef.current = value;
    if (draggingRef.current) return;
    // A tap's lingering bubble shows that tap's value; an unrelated update
    // ends it before the new value is painted.
    const owner = bubbleOwnerValueRef.current;
    if (owner !== null && owner !== value) hideBubbleNow();
    reconcileValue();
  }, [value, reconcileValue, hideBubbleNow]);

  const valueFromClientX = useCallback(
    (clientX: number) => {
      const track = trackRef.current;
      if (!track) return lastEmittedRef.current;
      const rect = track.getBoundingClientRect();
      const w = rect.width;
      if (w <= 0) return lastEmittedRef.current;
      const x = Math.max(0, Math.min(w, clientX - rect.left));
      return min + (x / w) * range;
    },
    [min, range]
  );

  const roundValue = useCallback(
    (v: number) => {
      const clamped = Math.max(min, Math.min(max, v));
      return clampToIntegerRange(
        alignSegmentMarksToIntegerValues
          ? roundHalfToEven(clamped)
          : Math.round(clamped),
        min,
        max
      );
    },
    [alignSegmentMarksToIntegerValues, min, max]
  );

  const commit = useCallback(
    (next: number) => {
      applyVisual(next);
      if (next !== lastEmittedRef.current) {
        lastEmittedRef.current = next;
        onChange(next);
      }
    },
    [applyVisual, onChange]
  );

  const emit = useCallback(
    (rawValue: number) => commit(roundValue(rawValue)),
    [commit, roundValue]
  );

  const snapToStep = useCallback(
    (idx: number) => {
      if (!hasSegments) return;
      commit(segmentIndexToValue(idx));
    },
    [hasSegments, segmentIndexToValue, commit]
  );

  const nearestMarkIndex = useCallback(
    (v: number) => {
      const pct = valueToPct(v);
      let best = 0;
      let bestDist = Infinity;
      for (let i = 0; i <= segments; i += 1) {
        const d = Math.abs(segmentIndexToPct(i) - pct);
        if (d < bestDist) {
          bestDist = d;
          best = i;
        }
      }
      return best;
    },
    [segments, segmentIndexToPct, valueToPct]
  );

  /**
   * Ends the active interaction: releases capture, hides the bubble (or lets
   * it linger after a tap), reconciles a value the parent changed during the
   * drag (without emitting `onChange`) and fires `onSlideComplete`.
   */
  const endInteraction = useCallback(
    (tap = false) => {
      const pointerId = pointerIdRef.current;
      pointerIdRef.current = null;
      draggingRef.current = false;
      if (pointerId !== null) {
        try {
          containerRef.current?.releasePointerCapture(pointerId);
        } catch {
          /* ignore */
        }
      }
      // The tap's own value, before the parent's latest value is reconciled.
      const tapValue = lastEmittedRef.current;
      // Repaint even when the value matched: marks or colors may have changed.
      reconcileValue();
      if (tap) {
        lingerThenHideBubble(tapValue);
      } else {
        setBubbleVisible(false);
      }
      onSlideComplete?.();
    },
    [onSlideComplete, reconcileValue, setBubbleVisible, lingerThenHideBubble]
  );

  const handlePointerDown = useCallback(
    (e: ReactPointerEvent<HTMLDivElement>) => {
      // Primary button only, and one pointer per interaction: secondary
      // buttons and extra fingers during a drag are ignored.
      if (disabled || draggingRef.current || e.button !== 0) return;
      const target = e.currentTarget;
      let captured = false;
      try {
        target.setPointerCapture(e.pointerId);
        pointerIdRef.current = e.pointerId;
        captured = true;
      } catch {
        /* setPointerCapture can throw on some browsers; ignore */
      }
      onSlideStart?.();
      pressStartXRef.current = e.clientX;
      pressLastXRef.current = e.clientX;
      pressStartTimeRef.current = Date.now();
      hasDraggedRef.current = false;
      // A press on a mark always lands exactly on that mark.
      const mark =
        e.target instanceof Element
          ? e.target.closest('[data-segment-mark]')
          : null;
      const markIndex = mark?.getAttribute('data-segment-mark') ?? null;
      pressedMarkRef.current = markIndex !== null;
      // A deferred snap-mode tap shows no bubble yet, and a previous tap's
      // bubble is hidden: either would show the old value. It appears once
      // the press becomes a drag or the tap lands.
      if (snapTapsActive && markIndex === null) {
        setBubbleVisible(false);
      } else {
        setBubbleVisible(true);
      }
      if (markIndex !== null) {
        snapToStep(Number(markIndex));
      } else if (!snapTapsActive) {
        emit(valueFromClientX(e.clientX));
      }
      if (!captured) {
        // Without capture, move/up may never arrive; treat as a single tap.
        if (snapTapsActive && markIndex === null) {
          snapToStep(nearestMarkIndex(valueFromClientX(e.clientX)));
        }
        endInteraction(true);
        return;
      }
      draggingRef.current = true;
    },
    [
      disabled,
      onSlideStart,
      endInteraction,
      setBubbleVisible,
      snapTapsActive,
      snapToStep,
      nearestMarkIndex,
      emit,
      valueFromClientX,
    ]
  );

  const handlePointerMove = useCallback(
    (e: ReactPointerEvent<HTMLDivElement>) => {
      if (!draggingRef.current || e.pointerId !== pointerIdRef.current) return;
      pressLastXRef.current = e.clientX;
      if (
        !hasDraggedRef.current &&
        Math.abs(e.clientX - pressStartXRef.current) > TAP_MOVE_THRESHOLD
      ) {
        hasDraggedRef.current = true;
        if (snapTapsActive && !pressedMarkRef.current) setBubbleVisible(true);
      }
      // Still a tap candidate in snap mode: keep the thumb put.
      if (snapTapsActive && !hasDraggedRef.current) return;
      emit(valueFromClientX(e.clientX));
    },
    [emit, snapTapsActive, setBubbleVisible, valueFromClientX]
  );

  const finishDrag = useCallback(
    (e: ReactPointerEvent<HTMLDivElement>) => {
      if (!draggingRef.current || e.pointerId !== pointerIdRef.current) return;
      // A quick tap (no drag) in snap mode lands on the nearest mark; a long
      // press does nothing; a drag keeps its free value. A press that started
      // on a mark already landed on it.
      let isTap = !hasDraggedRef.current && e.type === 'pointerup';
      if (isTap && snapTapsActive && !pressedMarkRef.current) {
        if (Date.now() - pressStartTimeRef.current <= TAP_MAX_DURATION_MS) {
          snapToStep(nearestMarkIndex(valueFromClientX(pressLastXRef.current)));
        } else {
          // Long press in snap mode changes nothing; no value to show.
          isTap = false;
        }
      }
      endInteraction(isTap);
    },
    [
      endInteraction,
      snapTapsActive,
      snapToStep,
      nearestMarkIndex,
      valueFromClientX,
    ]
  );

  // Next segment index strictly above (dir 1) or below (dir -1) `v`, derived
  // from the mark values so repeated steps never drift off the marks.
  const adjacentSegmentIndex = useCallback(
    (v: number, dir: 1 | -1) => {
      if (dir > 0) {
        for (let i = 0; i <= segments; i += 1) {
          if (segmentIndexToValue(i) > v) return i;
        }
      } else {
        for (let i = segments; i >= 0; i -= 1) {
          if (segmentIndexToValue(i) < v) return i;
        }
      }
      return null;
    },
    [segments, segmentIndexToValue]
  );

  const handleKeyDown = useCallback(
    (e: ReactKeyboardEvent<HTMLDivElement>) => {
      if (disabled || draggingRef.current) return;
      let dir: 1 | -1 | 0 = 0;
      if (e.key === 'ArrowRight' || e.key === 'ArrowUp') dir = 1;
      else if (e.key === 'ArrowLeft' || e.key === 'ArrowDown') dir = -1;
      else if (e.key !== 'Home' && e.key !== 'End') return;
      e.preventDefault();
      // A key press is not a tap: drop a tap's bubble before the value moves.
      hideBubbleNow();
      // Each accepted key press is one complete interaction.
      onSlideStart?.();
      const current = lastEmittedRef.current;
      if (e.key === 'Home') {
        emit(min);
      } else if (e.key === 'End') {
        emit(max);
      } else if (hasSegments && dir !== 0) {
        // Step by segment when segmented.
        const idx = adjacentSegmentIndex(current, dir);
        if (idx !== null) snapToStep(idx);
      } else {
        // Step by 1 unit when continuous.
        emit(current + dir);
      }
      endInteraction();
    },
    [
      disabled,
      onSlideStart,
      endInteraction,
      hideBubbleNow,
      hasSegments,
      adjacentSegmentIndex,
      snapToStep,
      emit,
      min,
      max,
    ]
  );

  // Turning the bubble on during a drag shows it with the drag's value; a
  // snap-mode tap candidate stays hidden until it becomes a drag or lands.
  useLayoutEffect(() => {
    if (!showBubble || !draggingRef.current) return;
    if (snapTapsActive && !hasDraggedRef.current && !pressedMarkRef.current) {
      return;
    }
    applyVisual(lastEmittedRef.current);
    setBubbleVisible(true);
  }, [showBubble, snapTapsActive, applyVisual, setBubbleVisible]);

  // Disabling mid-drag cancels the interaction so later moves or a pending
  // tap release cannot change the value.
  useEffect(() => {
    if (disabled && draggingRef.current) endInteraction();
  }, [disabled, endInteraction]);

  const handleFocus = useCallback((e: ReactFocusEvent<HTMLDivElement>) => {
    // Focus ring only for keyboard focus, not mouse clicks.
    const node = e.currentTarget;
    try {
      setFocusVisible(node.matches(':focus-visible'));
    } catch {
      setFocusVisible(false);
    }
  }, []);

  const handleBlur = useCallback(() => setFocusVisible(false), []);

  const registerMarkRef = useCallback(
    (idx: number, el: HTMLDivElement | null) => {
      markRefs.current[idx] = el;
    },
    []
  );

  const markIndices = useMemo(() => {
    const arr: number[] = [];
    if (!hasSegments) return arr;
    for (let i = 0; i <= segments; i += 1) arr.push(i);
    return arr;
  }, [hasSegments, segments]);

  const markHoverGlow = useMemo(
    () => `color-mix(in srgb, ${markActiveColor} 25%, transparent)`,
    [markActiveColor]
  );

  const containerStyle = useMemo<CSSProperties>(
    () => ({
      position: 'relative',
      width: '100%',
      height: HIT_AREA_HEIGHT,
      cursor: disabled ? 'not-allowed' : 'pointer',
      userSelect: 'none',
      touchAction: 'none',
      opacity: disabled ? 0.5 : 1,
      // Keyboard focus ring follows the fill color.
      outline: focusVisible ? `2px solid ${fillColor}` : 'none',
      outlineOffset: 2,
      borderRadius: 4,
    }),
    [disabled, focusVisible, fillColor]
  );

  const trackStyle = useMemo<CSSProperties>(
    () => ({
      position: 'absolute',
      top: '50%',
      left: THUMB_SIZE / 2,
      right: THUMB_SIZE / 2,
      transform: 'translateY(-50%)',
      height: sliderHeight,
      background: trackColor,
      borderRadius: sliderHeight / 2,
      pointerEvents: 'none',
    }),
    [sliderHeight, trackColor]
  );

  const fillStyle = useMemo<CSSProperties>(
    () => ({
      position: 'absolute',
      top: 0,
      left: 0,
      width: '0%',
      height: '100%',
      background: fillColor,
      borderRadius: sliderHeight / 2,
      pointerEvents: 'none',
      willChange: 'left, width',
    }),
    [fillColor, sliderHeight]
  );

  const thumbWrapperStyle = useMemo<CSSProperties>(
    () => ({
      position: 'absolute',
      top: '50%',
      left: '0%',
      transform: 'translate(-50%, -50%)',
      pointerEvents: 'none',
      willChange: 'left',
      zIndex: THUMB_Z_INDEX,
    }),
    []
  );

  const thumbStyle = useMemo<CSSProperties>(
    () => ({
      width: THUMB_SIZE,
      height: THUMB_SIZE,
      borderRadius: '50%',
      background: thumbColor,
      border: `1px solid ${thumbBorderColor}`,
      boxShadow: '0 1px 2px rgba(0, 0, 0, 0.08)',
      boxSizing: 'border-box',
    }),
    [thumbColor, thumbBorderColor]
  );

  const bubbleStyle = useMemo<CSSProperties>(
    () => ({
      position: 'absolute',
      bottom: '100%',
      left: '0%',
      marginBottom: 8,
      transform: 'translateX(-50%)',
      padding: '2px 8px',
      background: bubbleColor,
      color: bubbleTextColor,
      borderRadius: 4,
      fontSize: 12,
      lineHeight: '16px',
      fontWeight: 600,
      opacity: 0,
      transition: 'opacity 150ms ease',
      pointerEvents: 'none',
      whiteSpace: 'nowrap',
      zIndex: THUMB_Z_INDEX,
    }),
    [bubbleColor, bubbleTextColor]
  );

  return (
    <div
      ref={containerRef}
      role="slider"
      aria-label={ariaLabel}
      aria-labelledby={ariaLabelledBy}
      aria-valuemin={min}
      aria-valuemax={max}
      aria-valuenow={Math.round(value)}
      aria-disabled={disabled}
      tabIndex={disabled ? -1 : 0}
      onKeyDown={handleKeyDown}
      onPointerDown={handlePointerDown}
      onPointerMove={handlePointerMove}
      onPointerUp={finishDrag}
      onPointerCancel={finishDrag}
      onLostPointerCapture={finishDrag}
      onFocus={handleFocus}
      onBlur={handleBlur}
      style={containerStyle}
    >
      <div ref={trackRef} style={trackStyle}>
        <div ref={fillRef} style={fillStyle} />

        {markIndices.map((idx) => (
          <SegmentMark
            key={idx}
            index={idx}
            pct={segmentIndexToPct(idx)}
            hoverGlowColor={markHoverGlow}
            disabled={disabled}
            registerRef={registerMarkRef}
          />
        ))}

        <div ref={thumbRef} data-segment-thumb="" style={thumbWrapperStyle}>
          <div style={thumbStyle} />
        </div>

        {showBubble ? (
          <div ref={bubbleRef} data-segment-bubble="" style={bubbleStyle} />
        ) : null}
      </div>
    </div>
  );
}

export const SegmentSlider = memo(SegmentSliderComponent);
SegmentSlider.displayName = 'SegmentSlider';
