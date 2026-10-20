import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeCaptureProtection } from './ReactNativeCaptureProtection.nitro';

// Adapted from react-native-capture-protection 2.3.0; see LICENSE.upstream.
export enum CaptureEventType {
  NONE = 0,
  RECORDING = 1,
  END_RECORDING = 2,
  CAPTURED = 3,
  APP_SWITCHING = 4,
  UNKNOWN = 5,
  ALLOW = 8,
  PREVENT_SCREEN_CAPTURE = 16,
  PREVENT_SCREEN_RECORDING = 32,
  PREVENT_SCREEN_APP_SWITCHING = 64,
}

type CaptureCallback = (event: CaptureEventType) => void;
let native: ReactNativeCaptureProtection | undefined;
const subscribers = new Map<symbol, CaptureCallback>();

function getNative(): ReactNativeCaptureProtection {
  native ??= NitroModules.createHybridObject<ReactNativeCaptureProtection>(
    'ReactNativeCaptureProtection'
  );
  return native;
}

export const CaptureProtection = {
  prevent(): Promise<void> {
    return getNative().prevent();
  },
  allow(): Promise<void> {
    return getNative().allow();
  },
  addListener(callback: CaptureCallback): { remove(): void } {
    if (subscribers.size >= 128) {
      throw new Error('Capture protection supports at most 128 subscribers.');
    }
    const id = Symbol();
    subscribers.set(id, callback);
    if (subscribers.size === 1) {
      try {
        getNative().setListener((event) => {
          // Read current subscriptions so queued events cannot reach removed callers.
          for (const [key, listener] of Array.from(subscribers)) {
            if (subscribers.has(key)) {
              listener(event as CaptureEventType);
            }
          }
        });
      } catch (error) {
        subscribers.delete(id);
        throw error;
      }
    }
    return {
      remove() {
        if (subscribers.delete(id) && subscribers.size === 0) {
          getNative().setListener(undefined);
        }
      },
    };
  },
};
