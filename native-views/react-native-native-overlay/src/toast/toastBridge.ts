import type {
  IOverlayToastDismissReason,
  IOverlayToastEvent,
  IOverlayToastItem,
} from './types';

/** How a toast that never times out crosses the bridge: JSON has no Infinity. */
export const TOAST_DURATION_INFINITE = -1;

const DISMISS_REASONS: readonly IOverlayToastDismissReason[] = [
  'timeout',
  'swipe',
  'close',
  'programmatic',
];

export function serializeToastItems(
  items: readonly IOverlayToastItem[]
): string {
  return JSON.stringify(
    items.map((item) => ({
      ...item,
      spec: {
        ...item.spec,
        duration: Number.isFinite(item.spec.duration)
          ? item.spec.duration
          : TOAST_DURATION_INFINITE,
      },
    }))
  );
}

function toDismissReason(value: string): IOverlayToastDismissReason {
  return DISMISS_REASONS.find((reason) => reason === value) ?? 'programmatic';
}

export function toToastEvent(native: {
  toastId: string;
  generation?: number;
  type: string;
  value: string;
}): IOverlayToastEvent | undefined {
  const { toastId: id, generation, type, value } = native;
  const base =
    typeof generation === 'number' && Number.isFinite(generation)
      ? { id, generation }
      : { id };
  switch (type) {
    case 'dismiss':
      return { ...base, type, reason: toDismissReason(value) };
    case 'close':
      return { ...base, type, reason: toDismissReason(value) };
    case 'action':
      return { ...base, type, actionId: value };
    case 'link':
      return { ...base, type, link: value };
    case 'press':
      return { ...base, type };
    default:
      return undefined;
  }
}
