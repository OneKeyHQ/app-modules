import {
  NATIVE_MOTION_PRESETS,
  WEB_MOTION_PRESETS,
} from '../animation/motionPresets';
import { resolveOverlayAnimation } from '../animation/resolveAnimation';
import {
  springDampingRatio,
  springProgressAt,
  springSettleTimeMs,
  springToAndroidSpringForce,
  springToCssLinear,
} from '../animation/spring';
import { buildTransitionKeyframes } from '../web/animateTransition';

import type { IOverlaySpring } from '../animation/types';

const QUICK: IOverlaySpring = { mass: 0.1, stiffness: 100, damping: 20 };

describe('spring math', () => {
  it('maps Tamagui quick to an overdamped Android SpringForce', () => {
    const force = springToAndroidSpringForce(QUICK);
    expect(force.stiffness).toBeCloseTo(1000);
    expect(force.dampingRatio).toBeCloseTo(3.162, 3);
  });

  it('starts at 0 and converges to 1 for every damping regime', () => {
    const regimes: IOverlaySpring[] = [
      QUICK,
      { mass: 1, stiffness: 100, damping: 20 },
      { mass: 0.9, stiffness: 100, damping: 10 },
    ];
    expect(springDampingRatio({ mass: 1, stiffness: 100, damping: 20 })).toBe(
      1
    );
    for (const spring of regimes) {
      expect(springProgressAt(spring, 0)).toBeCloseTo(0, 6);
      expect(springProgressAt(spring, 5000)).toBeCloseTo(1, 4);
    }
  });

  it('overshoots when underdamped unless clamped', () => {
    const medium = { mass: 0.9, stiffness: 100, damping: 10 };
    const peak = Math.max(
      ...Array.from({ length: 1000 }, (_, t) => springProgressAt(medium, t))
    );
    expect(peak).toBeGreaterThan(1.1);
    const clamped = { ...medium, overshootClamping: true };
    const clampedPeak = Math.max(
      ...Array.from({ length: 1000 }, (_, t) => springProgressAt(clamped, t))
    );
    expect(clampedPeak).toBeLessThanOrEqual(1);
  });

  it('settles quick in roughly 1.3s at 0.1% precision', () => {
    const settle = springSettleTimeMs(QUICK);
    expect(settle).toBeGreaterThan(1200);
    expect(settle).toBeLessThan(1500);
    expect(springProgressAt(QUICK, 600)).toBeGreaterThan(0.95);
  });

  it('samples a CSS linear() curve ending at 1', () => {
    const { durationMs, easing } = springToCssLinear(QUICK, 10);
    expect(durationMs).toBe(springSettleTimeMs(QUICK));
    expect(easing.startsWith('linear(0, ')).toBe(true);
    expect(easing.endsWith(', 1)')).toBe(true);
    expect(easing.split(',')).toHaveLength(11);
  });
});

describe('buildTransitionKeyframes', () => {
  const extent = { top: 120, bottom: 480, left: 300, right: 700 };
  const sampled = {
    opacity: 0.35,
    transform: 'translate3d(0, 0, 0) scale(0.9)',
  };

  it('keeps a fade-free exit fully opaque after an interrupted fade-in', () => {
    const { exit } = resolveOverlayAnimation(
      'center',
      undefined,
      WEB_MOTION_PRESETS
    );
    const [start, end] = buildTransitionKeyframes({
      transition: exit,
      extent,
      direction: 'out',
      sampled,
    });
    // A partly transparent frame from the interrupted fade-in is dropped.
    expect(start.opacity).toBe(1);
    expect(end.opacity).toBe(1);
    // The interrupted scale is retargeted, not restarted.
    expect(start.transform).toBe(sampled.transform);
    expect(end.transform).toBe('translate3d(0, 0px, 0) scale(0.96)');
  });

  it('starts a fading exit from the interrupted opacity', () => {
    const { exit } = resolveOverlayAnimation(
      'center',
      { exit: { type: 'fade' } },
      WEB_MOTION_PRESETS
    );
    const [start, end] = buildTransitionKeyframes({
      transition: exit,
      extent,
      direction: 'out',
      sampled,
    });
    expect(start.opacity).toBe(sampled.opacity);
    expect(end.opacity).toBe(0);
  });

  it('reverses an interrupted fade-in from its rendered frame', () => {
    const { enter } = resolveOverlayAnimation(
      'center',
      undefined,
      WEB_MOTION_PRESETS
    );
    const [start] = buildTransitionKeyframes({
      transition: enter,
      extent,
      direction: 'in',
      sampled,
    });
    expect(start.opacity).toBe(sampled.opacity);
    expect(start.transform).toBe(sampled.transform);
  });
});

describe('resolveOverlayAnimation', () => {
  it('uses the Dialog center defaults with native springs', () => {
    const resolved = resolveOverlayAnimation(
      'center',
      undefined,
      NATIVE_MOTION_PRESETS
    );
    expect(resolved.enter).toMatchObject({
      type: 'scale',
      scale: 0.85,
      fade: true,
    });
    expect(resolved.enter.motion).toEqual(NATIVE_MOTION_PRESETS.quick);
    // OK-66647: the card must stay opaque while it shrinks on close, otherwise
    // the opaque content behind it shows through the surface as a ghost.
    expect(resolved.exit).toMatchObject({
      type: 'scale',
      scale: 0.96,
      fade: false,
    });
    expect(resolved.exit.motion).toEqual(NATIVE_MOTION_PRESETS.quick);
  });

  it('keeps the centered exit on the web quick curve without a fade', () => {
    const resolved = resolveOverlayAnimation(
      'center',
      undefined,
      WEB_MOTION_PRESETS
    );
    expect(resolved.exit.fade).toBe(false);
    expect(resolved.exit.motion).toEqual({
      type: 'timing',
      timing: { durationMs: 150, easing: [0.25, 0.1, 0.25, 1] },
    });
    // The backdrop still fades on the same clock.
    expect(resolved.backdrop.motion).toEqual(resolved.exit.motion);
  });

  it('mirrors a caller-supplied center enter when its exit is omitted', () => {
    const resolved = resolveOverlayAnimation(
      'center',
      { enter: { type: 'slide', edge: 'bottom', motion: 'fast' } },
      NATIVE_MOTION_PRESETS
    );
    expect(resolved.exit).toEqual(resolved.enter);
    expect(resolved.exit).toMatchObject({ type: 'slide', edge: 'bottom' });
    expect(resolved.exit.motion).toEqual(NATIVE_MOTION_PRESETS.fast);
  });

  it('mirrors the enter when a presentation has no default exit', () => {
    const resolved = resolveOverlayAnimation(
      'sheet',
      undefined,
      NATIVE_MOTION_PRESETS
    );
    expect(resolved.exit).toEqual(resolved.enter);
  });

  it('lets a caller override the centered default exit', () => {
    const resolved = resolveOverlayAnimation(
      'center',
      { exit: { type: 'fade', motion: 'fast' } },
      NATIVE_MOTION_PRESETS
    );
    expect(resolved.enter).toMatchObject({ type: 'scale', fade: true });
    expect(resolved.exit).toMatchObject({ type: 'fade' });
    expect(resolved.exit.motion).toEqual(NATIVE_MOTION_PRESETS.fast);
  });

  it('uses short web curves for the same preset names', () => {
    const resolved = resolveOverlayAnimation(
      'sheet',
      undefined,
      WEB_MOTION_PRESETS
    );
    expect(resolved.enter.motion).toEqual({
      type: 'timing',
      timing: { durationMs: 150, easing: [0.25, 0.1, 0.25, 1] },
    });
  });

  it('lets the lock screen appear instantly and fade out', () => {
    const resolved = resolveOverlayAnimation(
      'fullscreen',
      { enter: { type: 'none' }, exit: { type: 'fade', motion: 'lockFade' } },
      NATIVE_MOTION_PRESETS
    );
    expect(resolved.enter.type).toBe('none');
    expect(resolved.exit.motion).toEqual(NATIVE_MOTION_PRESETS.lockFade);
  });

  it('accepts a custom spring and keeps the presentation fallback motion', () => {
    const custom = {
      type: 'spring',
      spring: { mass: 1, stiffness: 230, damping: 26 },
    } as const;
    const resolved = resolveOverlayAnimation(
      'toast',
      { enter: { type: 'scale', scale: 0.8, offsetY: -20, motion: custom } },
      NATIVE_MOTION_PRESETS
    );
    expect(resolved.enter.motion).toEqual(custom);
    expect(resolved.enter.fade).toBe(true);
    expect(resolved.backdrop.motion).toEqual(NATIVE_MOTION_PRESETS.quick);
  });
});
