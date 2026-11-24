import type { QrCameraInfo, QrCameraPosition } from '../QrCameraList.nitro';

export type { QrCameraInfo, QrCameraPosition };

const BACK = /\b(back|rear|environment)\b|后置|背面/i;
const FRONT = /\b(front|user|facetime|selfie)\b|前置|正面/i;

/** Guesses where a camera faces from its label; browsers expose no position. */
export function cameraPositionFromLabel(label: string): QrCameraPosition {
  if (BACK.test(label)) return 'back';
  if (FRONT.test(label)) return 'front';
  return 'external';
}

/**
 * Video inputs the browser offers. Labels are empty until the page has been
 * granted camera access once; those entries are named "Camera N".
 */
export async function listWebCameras(): Promise<QrCameraInfo[]> {
  const devices = (await navigator.mediaDevices?.enumerateDevices?.()) ?? [];
  return devices
    .filter((device) => device.kind === 'videoinput' && device.deviceId)
    .map((device, index) => ({
      id: device.deviceId,
      label: device.label || `Camera ${index + 1}`,
      position: cameraPositionFromLabel(device.label),
    }));
}

/** Calls `listener` when a camera is connected or disconnected. */
export function subscribeWebCameraChanges(listener: () => void): () => void {
  const mediaDevices = navigator.mediaDevices;
  if (!mediaDevices?.addEventListener) return () => {};
  mediaDevices.addEventListener('devicechange', listener);
  return () => mediaDevices.removeEventListener('devicechange', listener);
}
