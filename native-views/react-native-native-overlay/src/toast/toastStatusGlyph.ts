import type { IOverlayToastStatus } from './types';

type Point = readonly [number, number];

interface IToastStatusGlyph {
  strokeWidth: number;
  contours: readonly (readonly Point[])[];
  dot?: readonly [number, number, number];
  ring?: {
    radius: number;
    trackOpacity: number;
    arcFraction: number;
  };
}

// Centerlines in a 24-unit box, in drawing order. Each contour has its own
// progress, so the two error diagonals draw simultaneously.
export const TOAST_STATUS_GLYPHS: Record<
  IOverlayToastStatus,
  IToastStatusGlyph
> = {
  success: {
    strokeWidth: 3,
    contours: [
      [
        [6, 12.5],
        [10, 16.5],
        [18, 7.5],
      ],
    ],
  },
  error: {
    strokeWidth: 3,
    contours: [
      [
        [7.5, 7.5],
        [16.5, 16.5],
      ],
      [
        [16.5, 7.5],
        [7.5, 16.5],
      ],
    ],
  },
  warning: {
    strokeWidth: 3,
    contours: [
      [
        [12, 6],
        [12, 13],
      ],
    ],
    dot: [12, 18, 1.6],
  },
  info: {
    strokeWidth: 3,
    contours: [
      [
        [12, 11],
        [12, 18],
      ],
    ],
    dot: [12, 6.25, 1.6],
  },
  loading: {
    strokeWidth: 2.75,
    contours: [],
    ring: { radius: 8, trackOpacity: 0.25, arcFraction: 0.25 },
  },
};
