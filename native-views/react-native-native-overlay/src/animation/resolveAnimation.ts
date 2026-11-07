import type {
  IOverlayAnimation,
  IOverlayMotion,
  IOverlayMotionInput,
  IOverlayMotionPreset,
  IOverlayPresentation,
  IOverlayTransition,
} from './types';

export interface IResolvedOverlayTransition
  extends Omit<IOverlayTransition, 'motion'> {
  motion: IOverlayMotion;
}

export interface IResolvedOverlayAnimation {
  enter: IResolvedOverlayTransition;
  exit: IResolvedOverlayTransition;
  backdrop: { motion: IOverlayMotion };
}

/**
 * Defaults match today's visuals: Dialog center (scale 0.85 + fade in, then a
 * fade-free scale 0.96 out), Tamagui Sheet (slide from bottom, quick), message
 * toasts (slide from top, 300ms quad), Popover floating content (scale 0.95 +
 * fade, popoverQuick).
 *
 * `exit` defaults to the presentation's own exit below; presentations without
 * one mirror `enter`.
 */
export const DEFAULT_OVERLAY_ANIMATIONS: Record<
  IOverlayPresentation,
  {
    enter: IOverlayTransition;
    /** Mirrors `enter` when omitted. */
    exit?: IOverlayTransition;
    backdrop: IOverlayMotionPreset;
  }
> = {
  sheet: {
    enter: { type: 'slide', edge: 'bottom', motion: 'quick' },
    backdrop: 'quick',
  },
  center: {
    enter: { type: 'scale', scale: 0.85, fade: true, motion: 'quick' },
    // OK-66647: the centered card is a solid surface. Fading it out lets any
    // opaque content behind it (a text-heavy page, a list inside another
    // overlay) show through the card for the whole transition, which reads as
    // a doubled/ghosted render. Keep the card opaque while it shrinks and let
    // the host unmount it at the end of the exit.
    exit: { type: 'scale', scale: 0.96, fade: false, motion: 'quick' },
    backdrop: 'quick',
  },
  toast: {
    enter: { type: 'slide', edge: 'top', fade: true, motion: 'toastSlide' },
    backdrop: 'quick',
  },
  fullscreen: {
    enter: { type: 'fade', motion: 'quick' },
    backdrop: 'quick',
  },
  anchored: {
    enter: {
      type: 'scale',
      scale: 0.95,
      fade: true,
      origin: 'anchor',
      motion: 'popoverQuick',
    },
    backdrop: 'popoverQuick',
  },
};

function resolveMotion(
  input: IOverlayMotionInput | undefined,
  fallback: IOverlayMotionInput,
  presets: Record<IOverlayMotionPreset, IOverlayMotion>
): IOverlayMotion {
  const value = input ?? fallback;
  return typeof value === 'string' ? presets[value] : value;
}

function resolveTransition(
  transition: IOverlayTransition,
  fallbackMotion: IOverlayMotionInput,
  presets: Record<IOverlayMotionPreset, IOverlayMotion>
): IResolvedOverlayTransition {
  return {
    ...transition,
    fade: transition.fade ?? transition.type === 'scale',
    motion: resolveMotion(transition.motion, fallbackMotion, presets),
  };
}

/** Fills presentation defaults and turns preset names into concrete curves. */
export function resolveOverlayAnimation(
  presentation: IOverlayPresentation,
  animation: IOverlayAnimation | undefined,
  presets: Record<IOverlayMotionPreset, IOverlayMotion>
): IResolvedOverlayAnimation {
  const defaults = DEFAULT_OVERLAY_ANIMATIONS[presentation];
  const enterInput = animation?.enter ?? defaults.enter;
  const fallbackMotion = defaults.enter.motion ?? 'quick';
  const enter = resolveTransition(enterInput, fallbackMotion, presets);
  const exitInput = animation?.exit ?? defaults.exit ?? enterInput;
  // A presentation default exit keeps its own fallback motion instead of
  // inheriting the enter's.
  const exitFallbackMotion = defaults.exit?.motion ?? fallbackMotion;
  const exit = resolveTransition(exitInput, exitFallbackMotion, presets);
  return {
    enter,
    exit,
    backdrop: {
      motion: resolveMotion(
        animation?.backdrop?.motion,
        defaults.backdrop,
        presets
      ),
    },
  };
}
