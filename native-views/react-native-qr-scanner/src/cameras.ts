import { NitroModules } from 'react-native-nitro-modules';

import type { QrCameraInfo, QrCameraList } from './QrCameraList.nitro';

export type { QrCameraInfo, QrCameraPosition } from './QrCameraList.nitro';

let cameraList: QrCameraList | undefined;
const listeners = new Set<() => void>();

function getCameraList(): QrCameraList {
  cameraList ??= NitroModules.createHybridObject<QrCameraList>('QrCameraList');
  return cameraList;
}

/**
 * Cameras the live scanner can use: one back and one front camera on phones,
 * plus external cameras where the platform supports them. Pass an entry's
 * `id` as `QrScanner`'s `cameraId`.
 */
export function listCameras(): Promise<QrCameraInfo[]> {
  return getCameraList().listCameras();
}

/** Calls `listener` when a camera is connected or disconnected. */
export function subscribeCameraChanges(listener: () => void): () => void {
  listeners.add(listener);
  if (listeners.size === 1) {
    getCameraList().setOnChange(() => {
      listeners.forEach((each) => each());
    });
  }
  return () => {
    if (!listeners.delete(listener) || listeners.size > 0) return;
    getCameraList().setOnChange(undefined);
  };
}
