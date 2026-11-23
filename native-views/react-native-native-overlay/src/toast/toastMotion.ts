import { springToCssLinear } from '../animation/spring';

import type { IOverlaySpring } from '../animation/types';

/**
 * Shared RareUI-inspired geometry and spring motion parameters for message
 * toasts across Web, iOS and Android.
 *
 * Concentric corner rule: `CARD_RADIUS = TILE_RADIUS + CARD_PADDING` (21 = 11 + 10).
 */
export const TOAST_GEOMETRY = {
  cardPadding: 10,
  tileSize: 36,
  tileRadius: 11,
  cardRadius: 21,
  stackGap: 10,
  contentGap: 12,
  textGap: 2,
  noIconPaddingStart: 6,
  actionHeight: 36,
  actionRadius: 11,
} as const;

export const TOAST_MOTION = {
  /** Card entrance, stack expand/collapse and swipe settle-back spring. */
  cardSpring: { mass: 1, stiffness: 460, damping: 36 } as IOverlaySpring,
  /** Decisive exit timing aligned with TIME_BEFORE_UNMOUNT (200 ms). */
  cardExitMs: 200,
  cardExitEase: [0.32, 0.72, 0, 1] as const,
  /** Critically damped tile entrance spring (zeta ~= 1.004, ~40% faster than raw RareUI). */
  tileSpring: { mass: 1, stiffness: 620, damping: 50 } as IOverlaySpring,
  /** Snappy glyph pop spring (same zeta ~= 0.659 / 1.0385 overshoot, ~35% faster). */
  glyphSpring: { mass: 1, stiffness: 920, damping: 40 } as IOverlaySpring,
  /** Tight icon choreography synchronized with the snappier card landing (~390 ms total). */
  arrivalDelayMs: 155,
  tileLeadMs: 45, // tile begins at 110 ms (arrival - 45)
  strokeDrawMs: 180,
  strokeDelayOffsetMs: 55, // stroke draws 210..390 ms on enter
  strokeEase: [0, 0, 0.58, 1] as const,
  glyphLeaveMs: 90,
  glyphLeaveEase: [0.42, 0, 1, 1] as const,
  tileBumpMs: 260,
  tileBumpPeakScale: 1.07,
  tileBumpPeakFraction: 0.4,
  tileBumpEase: [0, 0, 0.58, 1] as const,
  tileTintMs: 140,
  tileTintEase: [0.4, 0, 0.2, 1] as const,
  contentSwapMs: 160,
  spinnerPeriodMs: 850,
} as const;

export function getToastWebSprings() {
  return {
    card: springToCssLinear(TOAST_MOTION.cardSpring, 48),
    tile: springToCssLinear(TOAST_MOTION.tileSpring, 48),
    glyph: springToCssLinear(TOAST_MOTION.glyphSpring, 48),
  };
}
