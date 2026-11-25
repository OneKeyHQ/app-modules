import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

/**
 * Camera preview plus live QR detection. Native code only reports raw frame
 * text; mode handling and BC-UR assembly run in JS through `./ur`, so every
 * platform shares one session implementation.
 */
export interface QrScannerProps extends HybridViewProps {
  /**
   * True runs the camera. False stops capture and preview and releases the
   * camera. Each false -> true starts a new capture generation.
   */
  active: boolean;
  /**
   * `QrCameraInfo.id` of the camera to use. Unset, or an id that is no longer
   * connected, uses the default (back) camera. Changing it while active
   * switches cameras in a new capture generation.
   */
  cameraId?: string;
  /** The capture is ready. The ID identifies the device actually opened. */
  onReady?: (generation: number, cameraId: string) => void;
  /**
   * Text of each QR symbol seen. Repeats of the same text within one
   * generation may be suppressed natively; JS dedupes anyway.
   */
  onFrameText?: (text: string, generation: number) => void;
  /**
   * Capture failure. `code` is one of `permission-denied`, `no-camera`,
   * `camera-busy`, `decoder-unavailable`.
   */
  onError?: (code: string, message: string, generation: number) => void;
}

export interface QrScannerMethods extends HybridViewMethods {}

export type QrScanner = HybridView<QrScannerProps, QrScannerMethods>;
