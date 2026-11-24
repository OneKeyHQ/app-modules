import {
  CameraDevice,
  CaptureOptions,
  CaptureReadyInfo,
  DecodeImageOptions,
  PreferredResolution,
  QrCapture,
  QrPreviewFrame,
  QrScannerError,
  createCapture,
  decodeImage,
  listCameras,
  shouldFallbackToWeb,
} from "../index.js";

const decodeOpts: DecodeImageOptions = { maxPixels: 12_000_000, engines: ["zxing"] };
const decoded: Promise<string | null> = decodeImage("C:\\tmp\\qr.png", decodeOpts);
const cameras: Promise<CameraDevice[]> = listCameras();
const resolution: PreferredResolution = "1080p";

const opts: CaptureOptions = {
  source: {
    kind: "camera",
    deviceId: "\\\\?\\usb#vid_0000",
    preferredResolution: resolution,
    lockFocusOnDetect: true,
  },
  onReady: (gen: number, info?: CaptureReadyInfo) => {
    void gen;
    void info?.width;
    void info?.height;
    void info?.fps;
    void info?.subtype;
    void info?.focusSupported;
    void info?.autoFocusSupported;
    void info?.exposureSupported;
  },
  onText: (text: string, gen: number) => {
    void text;
    void gen;
  },
  onError: (error: QrScannerError, gen: number) => {
    const fallback: boolean = shouldFallbackToWeb(error);
    void error.code;
    void fallback;
    void gen;
  },
  onEnded: (gen: number) => {
    void gen;
  },
  onFrame: (frame: QrPreviewFrame) => {
    const handle: Buffer = frame.ntHandle;
    void handle;
    frame.release();
  },
};

const capture: QrCapture = createCapture(opts);
capture.start();
capture.setDevice("\\\\?\\usb#vid_0001");
capture.setDevice(null);
capture.setDevice();
const lent: number = capture.lentFrames();
capture.stop();
capture.dispose();

void decoded;
void cameras;
void lent;
