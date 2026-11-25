import type * as mac from "./mac/index";
import type * as win from "./win/index";

export interface CaptureOptions extends Omit<win.CaptureOptions, "onReady" | "onFrame"> {
  /** Camera ID is the second argument on both platforms. Windows also supplies metadata. */
  onReady?: (generation: number, deviceId?: string, info?: win.CaptureReadyInfo) => void;
  onFrame?: (frame: mac.QrPreviewFrame | win.QrPreviewFrame) => void;
}

export function createCapture(options?: CaptureOptions): mac.QrCapture | win.QrCapture;
export const decodeImage: typeof win.decodeImage;
export const decodeImageSymbols: typeof win.decodeImageSymbols;
export const listCameras: typeof mac.listCameras | typeof win.listCameras;
export const shouldFallbackToWeb: typeof win.shouldFallbackToWeb | undefined;
