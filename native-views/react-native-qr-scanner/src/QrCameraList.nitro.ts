import type { HybridObject } from 'react-native-nitro-modules';

/** Where a camera faces. Phones list one back and one front entry. */
export type QrCameraPosition = 'back' | 'front' | 'external';

export interface QrCameraInfo {
  /** Stable while the camera stays connected; pass it as `cameraId`. */
  id: string;
  position: QrCameraPosition;
  /** Device name where the platform has one (iOS), else a generic label. */
  label: string;
}

/**
 * Cameras the live scanner can use. Phones expose one back camera (on iOS the
 * virtual device that switches lenses itself) and one front camera; iPad and
 * Android add external cameras.
 */
export interface QrCameraList
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  listCameras(): Promise<QrCameraInfo[]>;
  /** Called when a camera is connected or disconnected. `undefined` clears it. */
  setOnChange(listener?: () => void): void;
}
