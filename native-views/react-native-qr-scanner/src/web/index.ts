// DOM backend for Web, browser extension and Linux Electron. No React Native
// imports: this entry must load without the native runtime.
export { configureWebQrScanner } from './engines';
export type { WebQrScannerConfig } from './engines';
export { decodeQrImage } from './decodeQrImage';
export { createWebQrScanner } from './scanner';
export {
  cameraPositionFromLabel,
  listWebCameras,
  subscribeWebCameraChanges,
} from './cameras';
export type { QrCameraInfo, QrCameraPosition } from './cameras';
export type { WebQrScanner, WebQrScannerOptions, WebScanSource } from './scanner';
export { DEFAULT_MAX_IMAGE_PIXELS } from '../decodeQrImage.constants';
export { QrScannerError, toQrScannerError } from '../errors';
export type { QrScannerErrorCode } from '../errors';
export { createScanSession, createUrAssembler, DEFAULT_UR_LIMITS } from '../ur';
export type { ScanError, ScanMode, ScanResult, ScanSession, UrLimits } from '../ur';
